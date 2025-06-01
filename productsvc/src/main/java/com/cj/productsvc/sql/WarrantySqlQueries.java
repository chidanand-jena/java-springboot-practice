package com.cj.productsvc.sql;

public class WarrantySqlQueries {
    private static final String INSERT_SQL = """
        INSERT INTO warranty_info (duration_months, warranty_type, description)
        VALUES (?, ?, ?)
    """;
    public static final String FIND_BY_ID =
            "SELECT * FROM product_db.warranty_info WHERE id = ?";

    public static final String EXISTS_BY_ID = "SELECT COUNT(1) FROM warranty_info WHERE id = ?";
public static final String FIND_ALL_IDS="SELECT id FROM warranty_info";
}
