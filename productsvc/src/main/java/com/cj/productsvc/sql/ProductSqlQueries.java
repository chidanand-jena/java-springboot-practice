package com.cj.productsvc.sql;


public class ProductSqlQueries {
    public static final String FIND_ALL =
            "SELECT * FROM products";

    public static final String FIND_BY_ID =
            "SELECT * FROM products WHERE id = ?";

    public static final String INSERT_PRODUCT =
            "INSERT INTO products " +
                    "(name, brand, description, price, stock_quantity, warranty_id, created_at, updated_at, created_by, updated_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)";

    public static final String UPDATE_PRODUCT =
            "UPDATE products SET " +
                    "name = ?, brand = ?, description = ?, price = ?, stock_quantity = ?, warranty_id = ?, updated_at = NOW(), updated_by = ? " +
                    "WHERE id = ?";

    public static final String DELETE_BY_ID =
            "DELETE FROM products WHERE id = ?";
}
