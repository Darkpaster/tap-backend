package com.human.tapMMO.config;

import com.human.tapMMO.model.tables.Talent;
import com.human.tapMMO.repository.TalentRepository;
import com.human.tapMMO.runtime.game.buff.Buff;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class TalentDataInitializer {

    @Bean
    public CommandLineRunner initTalents(TalentRepository talentRepository) {
        return args -> {
            // Создаем таланты для каждой категории и подкатегории

            // Боевые таланты - Управление телом
            Talent martialArts = new Talent();
            martialArts.setName("Боевые искусства");
            martialArts.setDescription("Увеличивает урон и точность атак без оружия");
            martialArts.setLevel(1);
            martialArts.setMaxLevel(5);
            martialArts.setType(TalentType.PASSIVE);
            martialArts.setCategory(TalentCategory.COMBAT);
            martialArts.setSubCategory(TalentSubCategory.BODY_CONTROL);
            martialArts.setSpecialization(TalentSpecialization.NONE);

            Set<Requirement> martialArtsReqs = new HashSet<>();
            Requirement agilityReq = new Requirement();
            agilityReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            agilityReq.setAttributeName("agility");
            agilityReq.setRequiredValue(3);
            agilityReq.setDescription("Требуется ловкость 3-го уровня");
            martialArtsReqs.add(agilityReq);
            martialArts.setRequirements(martialArtsReqs);

            Set<Buff> martialArtsEffects = new HashSet<>();
            Buff damageEffect = new Effect();
            damageEffect.setAttributeAffected("unarmedDamage");
            damageEffect.setValue(1.2);
            damageEffect.setDescription("Увеличение урона без оружия на 20%");
            martialArtsEffects.add(damageEffect);
            martialArts.setEffects(martialArtsEffects);

            talentRepository.save(martialArts);

            // Боевые таланты - Здравомыслие
            Talent tacticalMind = new Talent();
            tacticalMind.setName("Тактическое мышление");
            tacticalMind.setDescription("Позволяет анализировать слабости противников");
            tacticalMind.setLevel(1);
            tacticalMind.setMaxLevel(3);
            tacticalMind.setType(TalentType.ACTIVE);
            tacticalMind.setCategory(TalentCategory.COMBAT);
            tacticalMind.setSubCategory(TalentSubCategory.RATIONALITY);
            tacticalMind.setSpecialization(TalentSpecialization.NONE);

            Set<Requirement> tacticalMindReqs = new HashSet<>();
            Requirement intReq = new Requirement();
            intReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            intReq.setAttributeName("intelligence");
            intReq.setRequiredValue(5);
            intReq.setDescription("Требуется интеллект 5-го уровня");
            tacticalMindReqs.add(intReq);
            tacticalMind.setRequirements(tacticalMindReqs);

            talentRepository.save(tacticalMind);

            // Боевые таланты - Магия
            Talent fireball = new Talent();
            fireball.setName("Огненный шар");
            fireball.setDescription("Призывает огненный шар, наносящий урон врагам");
            fireball.setLevel(1);
            fireball.setMaxLevel(5);
            fireball.setType(TalentType.ACTIVE);
            fireball.setCategory(TalentCategory.COMBAT);
            fireball.setSubCategory(TalentSubCategory.MAGIC);
            fireball.setSpecialization(TalentSpecialization.NONE);

            Set<Requirement> fireballReqs = new HashSet<>();
            Requirement manaReq = new Requirement();
            manaReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            manaReq.setAttributeName("mana");
            manaReq.setRequiredValue(10);
            manaReq.setDescription("Требуется 10 единиц маны");
            fireballReqs.add(manaReq);
            fireball.setRequirements(fireballReqs);

            talentRepository.save(fireball);

            // Небоевые таланты - Коммуникация - Торговля
            Talent bargaining = new Talent();
            bargaining.setName("Торговое мастерство");
            bargaining.setDescription("Улучшает цены при покупке и продаже товаров");
            bargaining.setLevel(1);
            bargaining.setMaxLevel(5);
            bargaining.setType(TalentType.PASSIVE);
            bargaining.setCategory(TalentCategory.NON_COMBAT);
            bargaining.setSubCategory(TalentSubCategory.COMMUNICATION);
            bargaining.setSpecialization(TalentSpecialization.TRADING);

            Set<Requirement> bargainingReqs = new HashSet<>();
            Requirement charismaReq = new Requirement();
            charismaReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            charismaReq.setAttributeName("charisma");
            charismaReq.setRequiredValue(3);
            charismaReq.setDescription("Требуется харизма 3-го уровня");
            bargainingReqs.add(charismaReq);

            Requirement tradingExpReq = new Requirement();
            tradingExpReq.setType(RequirementType.SKILL_EXPERIENCE);
            tradingExpReq.setAttributeName("trading");
            tradingExpReq.setRequiredValue(100);
            tradingExpReq.setDescription("Требуется 100 очков опыта в торговле");
            bargainingReqs.add(tradingExpReq);

            bargaining.setRequirements(bargainingReqs);

            talentRepository.save(bargaining);

            // Небоевые таланты - Коммуникация - Психология
            Talent empathy = new Talent();
            empathy.setName("Эмпатия");
            empathy.setDescription("Позволяет лучше понимать эмоции и мотивации NPC");
            empathy.setLevel(1);
            empathy.setMaxLevel(3);
            empathy.setType(TalentType.PASSIVE);
            empathy.setCategory(TalentCategory.NON_COMBAT);
            empathy.setSubCategory(TalentSubCategory.COMMUNICATION);
            empathy.setSpecialization(TalentSpecialization.PSYCHOLOGY);

            Set<Requirement> empathyReqs = new HashSet<>();
            Requirement wisdomReq = new Requirement();
            wisdomReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            wisdomReq.setAttributeName("wisdom");
            wisdomReq.setRequiredValue(4);
            wisdomReq.setDescription("Требуется мудрость 4-го уровня");
            empathyReqs.add(wisdomReq);
            empathy.setRequirements(empathyReqs);

            talentRepository.save(empathy);

            // Небоевые таланты - Искусство - Изобразительное искусство
            Talent painting = new Talent();
            painting.setName("Живопись");
            painting.setDescription("Позволяет создавать картины, которые можно продать или подарить");
            painting.setLevel(1);
            painting.setMaxLevel(5);
            painting.setType(TalentType.ACTIVE);
            painting.setCategory(TalentCategory.NON_COMBAT);
            painting.setSubCategory(TalentSubCategory.ART);
            painting.setSpecialization(TalentSpecialization.VISUAL_ART);

            Set<Requirement> paintingReqs = new HashSet<>();
            Requirement dexterityReq = new Requirement();
            dexterityReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            dexterityReq.setAttributeName("dexterity");
            dexterityReq.setRequiredValue(3);
            dexterityReq.setDescription("Требуется ловкость рук 3-го уровня");
            paintingReqs.add(dexterityReq);

            Requirement artExpReq = new Requirement();
            artExpReq.setType(RequirementType.SKILL_EXPERIENCE);
            artExpReq.setAttributeName("visualArt");
            artExpReq.setRequiredValue(50);
            artExpReq.setDescription("Требуется 50 очков опыта в изобразительном искусстве");
            paintingReqs.add(artExpReq);

            painting.setRequirements(paintingReqs);

            talentRepository.save(painting);

            // Небоевые таланты - Изобретения - Алхимия
            Talent basicAlchemy = new Talent();
            basicAlchemy.setName("Основы алхимии");
            basicAlchemy.setDescription("Позволяет создавать простые зелья");
            basicAlchemy.setLevel(1);
            basicAlchemy.setMaxLevel(5);
            basicAlchemy.setType(TalentType.ACTIVE);
            basicAlchemy.setCategory(TalentCategory.NON_COMBAT);
            basicAlchemy.setSubCategory(TalentSubCategory.INVENTION);
            basicAlchemy.setSpecialization(TalentSpecialization.ALCHEMY);

            Set<Requirement> alchemyReqs = new HashSet<>();
            Requirement intelligenceReq = new Requirement();
            intelligenceReq.setType(RequirementType.ATTRIBUTE_LEVEL);
            intelligenceReq.setAttributeName("intelligence");
            intelligenceReq.setRequiredValue(3);
            intelligenceReq.setDescription("Требуется интеллект 3-го уровня");
            alchemyReqs.add(intelligenceReq);

            Requirement alchemyExpReq = new Requirement();
            alchemyExpReq.setType(RequirementType.SKILL_EXPERIENCE);
            alchemyExpReq.setAttributeName("alchemy");
            alchemyExpReq.setRequiredValue(75);
            alchemyExpReq.setDescription("Требуется 75 очков опыта в алхимии");
            alchemyReqs.add(alchemyExpReq);

            basicAlchemy.setRequirements(alchemyReqs);

            talentRepository.save(basicAlchemy);

            // Создание связи между талантами (пример графа)
            // Добавляем "Основы алхимии" как предпосылку для более продвинутого таланта

            Talent advancedAlchemy = new Talent();
            advancedAlchemy.setName("Продвинутая алхимия");
            advancedAlchemy.setDescription("Позволяет создавать сложные зелья и эликсиры");
            advancedAlchemy.setLevel(1);
            advancedAlchemy.setMaxLevel(3);
            advancedAlchemy.setType(TalentType.ACTIVE);
            advancedAlchemy.setCategory(TalentCategory.NON_COMBAT);
            advancedAlchemy.setSubCategory(TalentSubCategory.INVENTION);
            advancedAlchemy.setSpecialization(TalentSpecialization.ALCHEMY);

            Set<Requirement> advAlchemyReqs = new HashSet<>();
            Requirement talentReq = new Requirement();
            talentReq.setType(RequirementType.TALENT_PREREQUISITE);
            talentReq.setAttributeName(basicAlchemy.getId().toString());
            talentReq.setRequiredValue(3); // Требуется 3-й уровень таланта "Основы алхимии"
            talentReq.setDescription("Требуется талант 'Основы алхимии' 3-го уровня");
            advAlchemyReqs.add(talentReq);

            advancedAlchemy.setRequirements(advAlchemyReqs);
            advancedAlchemy.getPrerequisites().add(basicAlchemy);

            talentRepository.save(advancedAlchemy);
        };
    }
}