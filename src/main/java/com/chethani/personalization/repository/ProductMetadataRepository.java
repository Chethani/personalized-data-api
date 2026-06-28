package com.chethani.personalization.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chethani.personalization.entity.ProductMetadata;

public interface ProductMetadataRepository extends JpaRepository<ProductMetadata, String> {}