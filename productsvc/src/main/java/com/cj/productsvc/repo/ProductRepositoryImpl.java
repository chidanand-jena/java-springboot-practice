package com.cj.productsvc.repo;

import com.cj.productsvc.model.Product;
import com.cj.productsvc.sql.ProductSqlQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final JdbcTemplate jdbcTemplate;

private final RowMapper<Product> productRowMapper = new RowMapper<Product>() {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long warrantyId = rs.getObject("warranty_id") != null ? rs.getLong("warranty_id") : null;
        return Product.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .brand(rs.getString("brand"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price"))
                .stockQuantity(rs.getInt("stock_quantity"))
                .warrantyId(warrantyId)
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .createdBy(rs.getString("created_by"))
                .updatedBy(rs.getString("updated_by"))
                .build();
    }
};//example of BeanPropertyRowMapper
    private final RowMapper<Product> productBeanPropertyRowMapper = new BeanPropertyRowMapper<>(Product.class);


    @Override
    public List<Product> findAll() {

        return  jdbcTemplate.query(ProductSqlQueries.FIND_ALL, productRowMapper);
    }

    @Override
    public Optional<Product> findById(Long id) {
        try {
            Product product = jdbcTemplate.queryForObject(ProductSqlQueries.FIND_BY_ID, productBeanPropertyRowMapper, id);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }


    @Override
    public Long save(Product product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement(ProductSqlQueries.INSERT_PRODUCT, new String[]{"id"});
            ps.setString(1, product.getName());
            ps.setString(2, product.getBrand());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQuantity());
            if(product.getWarrantyId() != null) {
                ps.setLong(6, product.getWarrantyId());
            } else {
                ps.setNull(6, java.sql.Types.BIGINT);
            }
            ps.setString(7, product.getCreatedBy());
            ps.setString(8, product.getUpdatedBy());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DataAccessException("Failed to retrieve generated key for product") {};
        }
        return key.longValue();
    }

    @Override
    public Optional<Product> update(Product product) {
        int rowsAffected = jdbcTemplate.update(ProductSqlQueries.UPDATE_PRODUCT,
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getWarrantyId(),
                product.getUpdatedBy(),
                product.getId());

        return rowsAffected==1? findById(product.getId()) : Optional.empty();
    }


    /*
    Is above way to pass ? or param Safe from SQL Injection?
    ✅ YES — When you use ? placeholders + parameters (as you're doing), JdbcTemplate uses PreparedStatement internally, which:
    Escapes the inputs
    Treats them as values, not executable SQL
    Prevents attackers from injecting harmful SQL

    So you're safe from SQL injection as long as you avoid string concatenation inside SQL queries.
    Bad (Unsafe) Practice – DO NOT DO as below:
    String query = "INSERT INTO products (name) VALUES ('" + product.getName() + "')";
    jdbcTemplate.execute(query); // ❌ Dangerous – SQL Injection possible
     */
@Override
public int deleteById(Long id) {
        return jdbcTemplate.update(ProductSqlQueries.DELETE_BY_ID, id);
        // you can async process with kafka topic and listener to move data from main table to archive asynchronously
    }
}
