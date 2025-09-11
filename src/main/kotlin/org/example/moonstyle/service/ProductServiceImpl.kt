package org.example.moonstyle.service

import org.example.moonstyle.entity.ProductEntity
import org.example.moonstyle.entity.dto.PageResponse
import org.example.moonstyle.entity.dto.ProductFilter
import org.example.moonstyle.entity.dto.ProductResponse
import org.example.moonstyle.repository.ProductRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProductServiceImpl(
    private val repository: ProductRepository
) : ProductService {
    
    override fun list(filter: ProductFilter): PageResponse<ProductResponse> {
        val pageable = buildPageable(filter)
        val page = repository.search(
            q = emptyToNull(filter.q),
            color = emptyToNull(filter.color),
            size = emptyToNull(filter.size),
            category = emptyToNull(filter.category),
            priceMin = filter.priceMin,
            priceMax = filter.priceMax,
            pageable = pageable
        )
        return PageResponse(
            content = page.content.map { it.toDto() },
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages
        )
    }
    
    override fun get(id: Int): ProductResponse {
        val p = repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        }
        return p.toDto()
    }
    
    private fun ProductEntity.toDto() = ProductResponse(
        id = this.id!!,
        title = this.title,
        price = this.price,
        color = this.color,
        size = this.size,
        category = this.category,
        imageUrl = this.imageUrl,
        stockQty = this.stockQty
    )
    
    private fun buildPageable(filter: ProductFilter): PageRequest {
        val page = if (filter.page < 0) 0 else filter.page
        val size = filter.sizePage.coerceIn(1, 50)
        val sort = parseSort(filter.sort)
        return PageRequest.of(page, size, sort)
    }
    
    private fun parseSort(sortParam: String?): Sort {
        if (sortParam.isNullOrBlank()) return Sort.unsorted()
        val parts = sortParam.split(",").map { it.trim() }
        val property = parts.getOrNull(0) ?: return Sort.unsorted()
        val dir = parts.getOrNull(1)?.lowercase()
        return if (dir == "desc") Sort.by(Sort.Direction.DESC, property)
        else Sort.by(Sort.Direction.ASC, property)
    }
    
    private fun emptyToNull(s: String?) = s?.takeIf { it.isNotBlank() }
}