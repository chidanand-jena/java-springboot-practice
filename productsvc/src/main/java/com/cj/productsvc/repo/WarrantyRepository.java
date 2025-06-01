package com.cj.productsvc.repo;


import com.cj.productsvc.model.WarrantyInfo;
import com.cj.productsvc.sql.WarrantySqlQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WarrantyRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<WarrantyInfo> warrantyInfoRowMapper = new RowMapper<WarrantyInfo>() {
        @Override
        public WarrantyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return WarrantyInfo.builder()
                    .id(rs.getLong("id"))
                    .durationMonths(rs.getInt("duration_months"))
                    .warrantyType(rs.getString("warranty_type"))
                    .description(rs.getString("description"))
                    .build();
        }
    };
    public Optional<WarrantyInfo> findById(Long id){
        try {
            WarrantyInfo warrantyInfo = jdbcTemplate.queryForObject(WarrantySqlQueries.FIND_BY_ID, warrantyInfoRowMapper, id);
            return Optional.ofNullable(warrantyInfo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
     public List<Long> findAllIds() {
            return jdbcTemplate.queryForList(WarrantySqlQueries.FIND_ALL_IDS, Long.class);
        }


    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(WarrantySqlQueries.EXISTS_BY_ID, Integer.class, id);
        return count != null && count ==1;
    }
}
