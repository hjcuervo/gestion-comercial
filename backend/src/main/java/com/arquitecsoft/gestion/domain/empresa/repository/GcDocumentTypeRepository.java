// === File: GcDocumentTypeRepository.java ===
package com.arquitecsoft.gestion.domain.empresa.repository;

import com.arquitecsoft.gestion.domain.empresa.entity.GcDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GcDocumentTypeRepository extends JpaRepository<GcDocumentType, String> {
    List<GcDocumentType> findAllByOrderByNombreAsc();
}
