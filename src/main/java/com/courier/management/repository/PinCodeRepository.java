package com.courier.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.courier.management.dto.PinCodeDTO;
import com.courier.management.entity.PinCode;
import com.courier.management.projection.PincodeSearchProjection;

public interface PinCodeRepository extends JpaRepository<PinCode, String> {

        // List<PinCode> findByPincodeStartingWith(String prefix);

        @Query(value = """
           SELECT p.pincode AS pincode,
                 c.dest_id AS destId,
                c.dest_name AS destName
        FROM pincode_tbl p
        JOIN center_tbl c ON c.dest_id = p.dest_id
        WHERE p.pincode LIKE CONCAT(:keyword, '%')
         OR c.dest_name LIKE CONCAT(:keyword, '%')
         ORDER BY
         CASE
        WHEN p.pincode LIKE CONCAT(:keyword, '%') THEN 1
        ELSE 2
         END
        LIMIT 8
        """, nativeQuery = true)
        List<PincodeSearchProjection> searchByPinOrName(String keyword);
}
