package com.mokah.veterinary.features.appointments.mapper;

import com.mokah.veterinary.features.adresses.model.Address;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.branches.dto.BranchResponse;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.dto.VeterinarianResponse;
import com.mokah.veterinary.features.veterinarians.mapper.VeterinarianMapperImpl;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class AppointmentMapperTest {

    private AppointmentMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new AppointmentMapperImpl();
        ReflectionTestUtils.setField(mapper, "veterinarianMapper", new VeterinarianMapperImpl());
    }

    private User user() {
        return User.builder()
                .externalId(UUID.randomUUID())
                .firstName("Ana")
                .lastName("Lopez")
                .email("ana.lopez@veterinary.com")
                .build();
    }

    private Branch branch() {
        Address address = Address.builder()
                .id(1L)
                .street("Main")
                .number("123")
                .city("Springfield")
                .province("Illinois")
                .country("USA")
                .build();

        return Branch.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .name("Central")
                .phone("555-1000")
                .email("central@veterinary.com")
                .openingTime(LocalTime.of(8, 0))
                .closingTime(LocalTime.of(18, 0))
                .address(address)
                .build();
    }

    private Veterinarian veterinarian(User user, Branch branch) {
        return Veterinarian.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .licenseNumber("LIC-001")
                .phone("555-2000")
                .active(true)
                .workStartTime(LocalTime.of(9, 0))
                .workEndTime(LocalTime.of(17, 0))
                .workDays(EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .branch(branch)
                .user(user)
                .build();
    }

    private Pet pet() {
        return Pet.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .name("Rex")
                .birthDate(LocalDate.of(2020, 5, 10))
                .active(true)
                .color("Brown")
                .build();
    }

    private Appointment appointment(Veterinarian veterinarian, Pet pet, Branch branch) {
        return Appointment.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .appointmentDate(LocalDateTime.of(2026, 3, 15, 10, 30))
                .durationMinutes(30)
                .reason("Checkup")
                .status(AppointmentStatus.CONFIRMED)
                .veterinarian(veterinarian)
                .pet(pet)
                .branch(branch)
                .build();
    }

    @Test
    void mapsPublicVeterinarianNamesFromUser() {
        User user = user();
        Veterinarian veterinarian = veterinarian(user, branch());

        AppointmentResponse response = mapper.toResponse(appointment(veterinarian, pet(), branch()));

        assertThat(response).isNotNull();
        assertThat(response.veterinarian()).isNotNull();
        assertThat(response.veterinarian().firstName()).isEqualTo("Ana");
        assertThat(response.veterinarian().lastName()).isEqualTo("Lopez");
        assertThat(response.veterinarian()).hasNoNullFieldsOrPropertiesExcept("workDays");
    }

    @Test
    void stillMapsAllOtherAppointmentAndVeterinarianFields() {
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(), branch);
        Pet pet = pet();
        Appointment appointment = appointment(veterinarian, pet, branch);

        AppointmentResponse response = mapper.toResponse(appointment);

        assertThat(response.externalId()).isEqualTo(appointment.getExternalId());
        assertThat(response.appointmentDate()).isEqualTo(appointment.getAppointmentDate());
        assertThat(response.durationMinutes()).isEqualTo(30);
        assertThat(response.reason()).isEqualTo("Checkup");
        assertThat(response.status()).isEqualTo(AppointmentStatus.CONFIRMED);

        PetResponse petResponse = response.pet();
        assertThat(petResponse.externalId()).isEqualTo(pet.getExternalId());
        assertThat(petResponse.name()).isEqualTo("Rex");
        assertThat(petResponse.birthDate()).isEqualTo(LocalDate.of(2020, 5, 10));
        assertThat(petResponse.color()).isEqualTo("Brown");
        assertThat(petResponse.active()).isTrue();

        var vetResponse = response.veterinarian();
        assertThat(vetResponse.externalId()).isEqualTo(veterinarian.getExternalId());
        assertThat(vetResponse.workStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(vetResponse.workEndTime()).isEqualTo(LocalTime.of(17, 0));
        assertThat(vetResponse.workDays())
                .containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);

        BranchResponse branchResponse = response.branch();
        assertThat(branchResponse.externalId()).isEqualTo(branch.getExternalId());
        assertThat(branchResponse.name()).isEqualTo("Central");
        assertThat(branchResponse.phone()).isEqualTo("555-1000");
        assertThat(branchResponse.email()).isEqualTo("central@veterinary.com");
        assertThat(branchResponse.openingTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(branchResponse.closingTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(branchResponse.address()).isNotNull();
        assertThat(branchResponse.address().city()).isEqualTo("Springfield");

    }

    @Test
    void returnsNullVeterinarianWhenAppointmentHasNoVeterinarian() {
        Appointment appointment = appointment(null, pet(), branch());

        AppointmentResponse response = mapper.toResponse(appointment);

        assertThat(response).isNotNull();
        assertThat(response.veterinarian()).isNull();
    }

    @Test
    void handlesUserNullGracefully() {
        Veterinarian veterinarian = veterinarian(null, branch());

        AppointmentResponse response = mapper.toResponse(appointment(veterinarian, pet(), branch()));

        assertThat(response.veterinarian()).isNotNull();
        assertThat(response.veterinarian().firstName()).isNull();
        assertThat(response.veterinarian().lastName()).isNull();
        assertThat(response.veterinarian().externalId()).isEqualTo(veterinarian.getExternalId());
    }

    @Test
    void returnsNullWhenEntityIsNull() {
        assertThatCode(() -> assertThat(mapper.toResponse(null)).isNull())
                .doesNotThrowAnyException();
    }

    @Test
    void updateDoesNotModifyStatus() {
        Branch branch = branch();
        Veterinarian veterinarian = veterinarian(user(), branch);
        Pet pet = pet();
        Appointment appointment = appointment(veterinarian, pet, branch);

        AppointmentUpdateDTO dto = new AppointmentUpdateDTO(
                LocalDateTime.of(2026, 4, 1, 11, 0),
                45,
                "Updated reason",
                AppointmentStatus.PENDING,
                pet.getExternalId(),
                veterinarian.getExternalId(),
                branch.getExternalId()
        );

        mapper.update(appointment, dto);

        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(appointment.getAppointmentDate()).isEqualTo(dto.appointmentDate());
        assertThat(appointment.getDurationMinutes()).isEqualTo(dto.durationMinutes());
        assertThat(appointment.getReason()).isEqualTo(dto.reason());
    }
}
