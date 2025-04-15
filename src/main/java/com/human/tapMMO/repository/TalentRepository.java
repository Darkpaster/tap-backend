package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Talent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalentRepository extends JpaRepository<Talent, Long> {
//    List<Talent> findByCategory(TalentCategory category);
//    List<Talent> findBySubCategory(TalentSubCategory subCategory);
//    List<Talent> findBySpecialization(TalentSpecialization specialization);

//    @Query("SELECT t FROM Talent t WHERE t.id IN " +
//            "(SELECT p.id FROM Talent talent JOIN talent.prerequisites p WHERE talent.id = :talentId)")
//    List<Talent> findPrerequisitesForTalent(Long talentId);
//
//    @Query("SELECT t FROM Talent t WHERE :prerequisiteId IN " +
//            "(SELECT p.id FROM t.prerequisites p)")
//    List<Talent> findTalentsWithPrerequisite(Long prerequisiteId);
}
