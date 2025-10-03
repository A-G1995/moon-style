package org.example.moonstyle.service

import org.example.moonstyle.entity.dto.ProductDto
import org.example.moonstyle.entity.toDto
import org.example.moonstyle.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ProductServiceImpl(
    private val repo: ProductRepository
) : ProductService {
    
    override fun list(
        q: String?,
        color: String?,
        size: String?,
        category: String?,
        priceMin: Long?,
        priceMax: Long?
    ): List<ProductDto> {
        val entities = repo.search(q, color, size, category, priceMin, priceMax)
        return entities.map { it.toDto() }
    }
    
    override fun get(id: Int): ProductDto {
        val p = repo.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "محصول یافت نشد")
        }
        return p.toDto()
    }
}