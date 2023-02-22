package com.javappa.startappa.startappalight.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javappa.startappa.startappalight.application.domain.Item;

@Repository("LoginItemRepository")
public interface ItemRepository extends JpaRepository<Item, Long> {
	
	@Query("select max(fakeId) from Item ai where ai.creator.id=:creatorId")
	Long maxFakeId(@Param("creatorId") Long creatorId);	
}
