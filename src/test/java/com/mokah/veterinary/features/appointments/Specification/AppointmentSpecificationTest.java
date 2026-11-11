package com.mokah.veterinary.features.appointments.Specification;

import com.mokah.veterinary.features.appointments.model.Appointment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentSpecificationTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void nullPetExternalIdsDoesNotFilter() {
        From<Object, Appointment> from = mock(From.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate conjunction = mock(Predicate.class);

        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Predicate result = AppointmentSpecification.hasPetExternalIdIn(null)
                .toPredicate(from, criteriaBuilder);

        assertThat(result).isSameAs(conjunction);
        verify(from, never()).get("pet");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void emptyPetExternalIdsDeniesAccess() {
        From<Object, Appointment> from = mock(From.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate disjunction = mock(Predicate.class);

        when(criteriaBuilder.disjunction()).thenReturn(disjunction);

        Predicate result = AppointmentSpecification.hasPetExternalIdIn(List.of())
                .toPredicate(from, criteriaBuilder);

        assertThat(result).isSameAs(disjunction);
        verify(from, never()).get("pet");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void nonEmptyPetExternalIdsFilterByPetExternalId() {
        From<Object, Appointment> from = mock(From.class);
        Path<Object> petPath = mock(Path.class);
        Path<Object> externalIdPath = mock(Path.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate inPredicate = mock(Predicate.class);
        List<UUID> petExternalIds = List.of(UUID.randomUUID());

        when(from.get("pet")).thenReturn(petPath);
        when(petPath.get("externalId")).thenReturn(externalIdPath);
        when(externalIdPath.in(petExternalIds)).thenReturn(inPredicate);

        Predicate result = AppointmentSpecification.hasPetExternalIdIn(petExternalIds)
                .toPredicate(from, criteriaBuilder);

        assertThat(result).isSameAs(inPredicate);
        verify(externalIdPath).in(petExternalIds);
    }
}
