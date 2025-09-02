Users

Fields:
id (BIGINT), email (unique), password_hash, full_name, phone (nullable),
birth_date (DATE, nullable), national_number (nullable),
is_admin (BOOLEAN, default false), created_at (TIMESTAMP)

Notes: only “fake auth” (signup/login returning sessionId).

Indexes: unique on email.

Products

Fields:
id, sku (nullable), title, description (TEXT, nullable),
price (DECIMAL(10,2)), image_url (nullable),
color (VARCHAR), size (ENUM: XS,S,M,L,XL),
category (VARCHAR), attributes_json (JSON, nullable),
stock_qty (INT), is_active (BOOLEAN),
created_at, updated_at

Filters to support: q (title contains), color, size, category, priceMin, priceMax.

Indexes: composite (is_active, category, color, size, price) + single on price.

Carts

Cart: id, user_id, status (ACTIVE|CONVERTED), updated_at.

CartItem: id, cart_id, product_id, qty, unit_price_snapshot (DECIMAL(10,2)).

Rules: one ACTIVE cart per user (enforce in service), only add items for active products with enough stock.

Orders

Order: id, user_id, total (DECIMAL(10,2)), status (CREATED|PAID), created_at.

OrderItem: id, order_id, product_id, qty, unit_price_snapshot, line_total.

Flow: convert ACTIVE cart → order in a single transaction; decrement products.stock_qty.

Account (profile edits)

Editable fields: full_name, email (must stay unique), phone, birth_date, national_number.

Admin panel (via is_admin=true)

Views: all products, all purchases (orders), sales-by-category report, all members.

Report computation: sum order_items.line_total grouped by products.category (join).

SNNA (Spend-Based Recommendations)

Goal: Given amount N, return active, in-stock products priced near N.

Band: start at ±20% → expand to ±30%, ±40% if results < 10, stop there.

Ranking: sort by absolute price distance to N; optional tie-break by stock desc.

For user: N = last order total; fallback cart subtotal; else median active product price.

Diversity (optional): max 2 per category if you want variety.

Error & pagination conventions

Error JSON: { "code": "NOT_FOUND", "message": "Product not found" }

Pagination JSON: { "content":[...], "page":0, "size":20, "totalElements":123, "totalPages":7 }