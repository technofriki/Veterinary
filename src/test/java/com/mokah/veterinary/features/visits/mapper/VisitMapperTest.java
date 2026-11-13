package com.mokah.veterinary.features.visits.mapper;

import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapperImpl;
import com.mokah.veterinary.features.appointments.mapper.AppointmentMapperImpl;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.breed.mapper.BreedMapperImpl;
import com.mokah.veterinary.features.pets.mapper.PetMapperImpl;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapperImpl;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.model.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMapperTest {

    private VisitMapperImpl mapper;

    @BeforeEach
    void setUp() {
        VeterinarianMapperImpl veterinarianMapper = new VeterinarianMapperImpl();

        AppointmentMapperImpl appointmentMapper = new AppointmentMapperImpl();
        ReflectionTestUtils.setField(appointmentMapper, "veterinarianMapper", veterinarianMapper);

        AnimalTypeMapperImpl animalTypeMapper = new AnimalTypeMapperImpl();
        BreedMapperImpl breedMapper = new BreedMapperImpl();

        PetMapperImpl petMapper = new PetMapperImpl();
        ReflectionTestUtils.setField(petMapper, "animalTypeMapper", animalTypeMapper);
        ReflectionTestUtils.setField(petMapper, "breedMapper", breedMapper);

        mapper = new VisitMapperImpl();
        ReflectionTestUtils.setField(mapper, "veterinarianMapper", veterinarianMapper);
        ReflectionTestUtils.setField(mapper, "appointmentMapper", appointmentMapper);
        ReflectionTestUtils.setField(mapper, "petMapper", petMapper);
    }

    private User user() {
        return User.builder()
                .externalId(UUID.randomUUID())
                .firstName("Ana")
                .lastName("Lopez")
                .email("ana.lopez@veterinary.com")
                .build();
    }

    private Veterinarian veterinarian(User user) {
        return Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(true)
                .user(user)
                .build();
    }

    private Pet pet() {
        return Pet.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .name("Rex")
                .active(true)
                .color("Brown")
                .build();
    }

    @Test
    void nestedAppointmentVeterinarianIncludesUserFields() {
        User user = user();
        Veterinarian veterinarian = veterinarian(user);
        Pet pet = pet();

        Appointment appointment = Appointment.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .appointmentDate(LocalDateTime.of(2026, 3, 15, 10, 30))
                .durationMinutes(30)
                .reason("Checkup")
                .status(AppointmentStatus.COMPLETED)
                .veterinarian(veterinarian)
                .pet(pet)
                .build();

        LocalDateTime visitDate = LocalDateTime.of(2026, 3, 15, 10, 45);

        Visit visit = Visit.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .observations("All good")
                .appointment(appointment)
                .pet(pet)
                .veterinarian(veterinarian)
                .visitDate(visitDate)
                .build();

        VisitResponse response = mapper.toResponse(visit);

        assertThat(response.appointment()).isNotNull();
        assertThat(response.appointment().veterinarian()).isNotNull();
        assertThat(response.appointment().veterinarian().firstName()).isEqualTo("Ana");
        assertThat(response.appointment().veterinarian().lastName()).isEqualTo("Lopez");
        assertThat(response.appointment().veterinarian().externalId()).isEqualTo(veterinarian.getExternalId());

        assertThat(response.veterinarian()).isNotNull();
        assertThat(response.veterinarian().firstName()).isEqualTo("Ana");
        assertThat(response.veterinarian().lastName()).isEqualTo("Lopez");
        assertThat(response.veterinarian().externalId()).isEqualTo(veterinarian.getExternalId());

        assertThat(response.pet()).isNotNull();
        assertThat(response.pet().externalId()).isEqualTo(pet.getExternalId());
        assertThat(response.pet().name()).isEqualTo("Rex");

        assertThat(response.visitDate()).isNotNull();
        assertThat(response.visitDate()).isEqualTo(visitDate);
    }

    @Test
    void walkInVisitMapsPetAndVisitDate() {
        User user = user();
        Veterinarian veterinarian = veterinarian(user);
        Pet pet = pet();

        LocalDateTime visitDate = LocalDateTime.of(2026, 3, 15, 14, 30);

        Visit visit = Visit.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .observations("Emergency visit")
                .appointment(null)
                .pet(pet)
                .veterinarian(veterinarian)
                .visitDate(visitDate)
                .build();

        VisitResponse response = mapper.toResponse(visit);

        assertThat(response.appointment()).isNull();

        assertThat(response.veterinarian()).isNotNull();
        assertThat(response.veterinarian().firstName()).isEqualTo("Ana");
        assertThat(response.veterinarian().lastName()).isEqualTo("Lopez");

        assertThat(response.pet()).isNotNull();
        assertThat(response.pet().externalId()).isEqualTo(pet.getExternalId());
        assertThat(response.pet().name()).isEqualTo("Rex");

        assertThat(response.visitDate()).isNotNull();
        assertThat(response.visitDate()).isEqualTo(visitDate);
    }
}
