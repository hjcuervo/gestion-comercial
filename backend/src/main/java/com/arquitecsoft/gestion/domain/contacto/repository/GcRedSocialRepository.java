package com.arquitecsoft.gestion.domain.contacto.repository;

import com.arquitecsoft.gestion.domain.contacto.entity.GcRedSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcRedSocialRepository extends JpaRepository<GcRedSocial, String> {

    List<GcRedSocial> findByActivoOrderByOrdenAscNombreAsc(Integer activo);
}
