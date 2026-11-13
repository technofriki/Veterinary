package com.mokah.veterinary.features.appointments.service;

import com.mokah.veterinary.common.exception.AppointmentOverlapException;
import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.InvalidAppointmentTimeException;
import com.mokah.veterinary.common.exception.InvalidRequestParameterException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.appointments.Specification.AppointmentSpecification;
import com.mokah.veterinary.features.appointments.dto.AppointmentCreateDTO;
import com.mokah.veterinary.features.appointments.dto.AppointmentResponse;
import com.mokah.veterinary.features.appointments.dto.AppointmentUpdateDTO;
import com.mokah.veterinary.features.appointments.mapper.AppointmentMapper;
import com.mokah.veterinary.features.appointments.model.Appointment;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.repository.AppointmentRepository;
import com.mokah.veterinary.features.branches.model.Branch;
import com.mokah.veterinary.features.branches.service.BranchService;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.service.PetService;
import com.mokah.veterinary.features.receptionists.model.Receptionist;
import com.mokah.veterinary.features.receptionists.repository.ReceptionistRepository;
import com.mokah.veterinary.features.users.enums.UserState;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.veterinarians.model.Veterinarian;
import com.mokah.veterinary.features.veterinarians.repository.VeterinarianRepository;
import com.mokah.veterinary.security.model.Credentials;
import com.mokah.veterinary.security.repository.CredentialsRepository;
import com.mokah.veterinary.features.veterinarians.service.VeterinarianService;
import com.mokah.veterinary.security.enums.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final PetService petService;
    private final VeterinarianService veterinarianService;
    private final BranchService branchService;
    private final CredentialsRepository credentialsRepository;
    private final ReceptionistRepository receptionistRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerByPetRepository ownerByPetRepository;
    private final VeterinarianRepository veterinarianRepository;

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentCreateDTO dto) {

        validateClientPetOwnership(dto.petExternalId(), "Pet", dto.petExternalId());

        validatePastDate(dto.appointmentDate());
        validateBranchSchedule(dto.branchExternalId(), dto.appointmentDate());

        validateAppointmentOverlap(
                dto.veterinarianExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                null
        );

        validatePetOverlap(
                dto.petExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                null
        );

        validateHalfHourSlot(dto.appointmentDate());

        validateVeterinarianSchedule(
                dto.veterinarianExternalId(),
                dto.appointmentDate());

        veterinarianService.validateActive(dto.veterinarianExternalId());

        Branch branch = branchService.entityByExternalId(dto.branchExternalId());

        Appointment entity = mapper.toEntity(dto);

        entity.setStatus(AppointmentStatus.PENDING);
        entity.setPet(petService.entityByExternalId(dto.petExternalId()));
        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));
        entity.setBranch(branch);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public AppointmentResponse findById(UUID externalId) {
        Appointment appointment = entityByExternalId(externalId);

        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();

        if (clientPetExternalIds != null
                && !clientPetExternalIds.contains(appointment.getPet().getExternalId())) {
            throw new ResourceNotFoundException("Appointment", "externalId", externalId);
        }

        return mapper.toResponse(appointment);
    }

    @Override
    public Appointment entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "externalId", externalId));
    }

    @Override
    public List<AppointmentResponse> findAll(
            LocalDateTime date,
            LocalDate dateFrom,
            LocalDate dateTo,
            String reason,
            AppointmentStatus status,
            UUID petExternalId,
            UUID veterinarianExternalId) {

        if (date != null && (dateFrom != null || dateTo != null)) {
            throw new InvalidRequestParameterException("Cannot use 'date' parameter together with 'dateFrom' or 'dateTo'");
        }

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new InvalidRequestParameterException("dateFrom must be before or equal to dateTo");
        }

        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();
        UUID authenticatedVeterinarianExternalId = getVeterinarianExternalIdForCurrentUser();

        List<UUID> veterinarianExternalIds = null;
        if (authenticatedVeterinarianExternalId != null) {
            veterinarianExternalIds = List.of(authenticatedVeterinarianExternalId);
        } else if (veterinarianExternalId != null) {
            veterinarianExternalIds = List.of(veterinarianExternalId);
        }

        PredicateSpecification<Appointment> spec = PredicateSpecification.allOf(
                AppointmentSpecification.hasAppointmentDate(date),
                AppointmentSpecification.hasAppointmentDateBetween(dateFrom, dateTo),
                AppointmentSpecification.hasReason(reason),
                AppointmentSpecification.hasStatus(status),
                AppointmentSpecification.hasPetExternalId(petExternalId),
                AppointmentSpecification.hasVeterinarianExternalIdIn(veterinarianExternalIds),
                AppointmentSpecification.hasPetExternalIdIn(clientPetExternalIds)
        );

        return mapper.toResponseList(repository.findAll(spec));
    }

    @Override
    @Transactional
    public AppointmentResponse update(UUID externalId, AppointmentUpdateDTO dto) {

        Appointment entity = entityByExternalId(externalId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authorizeUpdate(entity, dto, authentication);

        validatePastDate(dto.appointmentDate());
        validateBranchSchedule(dto.branchExternalId(), dto.appointmentDate());

        if (dto.status() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Use DELETE endpoint to cancel appointments.");
        }

        if (dto.status() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Appointments can only be completed via Visit.");
        }

        if (dto.status() != entity.getStatus()) {
            throw new BusinessRuleException(
                    "Appointment status cannot be changed via update. Use the confirm endpoint."
            );
        }

        validateAppointmentOverlap(
                dto.veterinarianExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                externalId
        );

        validatePetOverlap(
                dto.petExternalId(),
                dto.appointmentDate(),
                dto.durationMinutes(),
                externalId
        );

        validateHalfHourSlot(dto.appointmentDate());
        validateVeterinarianSchedule(
                dto.veterinarianExternalId(),
                dto.appointmentDate());

        if (entity.getVeterinarian() == null
                || !entity.getVeterinarian().getExternalId().equals(dto.veterinarianExternalId())) {
            veterinarianService.validateActive(dto.veterinarianExternalId());
        }

        mapper.update(entity, dto);

        entity.setBranch(branchService.entityByExternalId(dto.branchExternalId()));
        entity.setPet(petService.entityByExternalId(dto.petExternalId()));
        entity.setVeterinarian(veterinarianService.entityByExternalId(dto.veterinarianExternalId()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {

        Appointment appointment = entityByExternalId(externalId);

        validateClientPetOwnership(
                appointment.getPet().getExternalId(),
                "Appointment",
                externalId);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot cancel a COMPLETED appointment.");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException("Appointment already cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        repository.save(appointment);
    }

    private void validateAppointmentOverlap(
            UUID veterinarianExternalId,
            LocalDateTime start,
            Integer durationMinutes,
            UUID currentAppointmentId) {

        LocalDateTime end = start.plusMinutes(durationMinutes);

        List<Appointment> appointments =
                repository.findByVeterinarian_ExternalIdAndStatusIn(
                        veterinarianExternalId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                );

        for (Appointment a : appointments) {

            if (currentAppointmentId != null
                    && a.getExternalId().equals(currentAppointmentId)) {
                continue;
            }

            LocalDateTime aStart = a.getAppointmentDate();
            LocalDateTime aEnd = aStart.plusMinutes(a.getDurationMinutes());

            boolean overlap = start.isBefore(aEnd) && end.isAfter(aStart);

            if (overlap) {
                throw new AppointmentOverlapException(
                        "Veterinarian already has an overlapping appointment."
                );
            }
        }
    }

    private void validatePetOverlap(
            UUID petExternalId,
            LocalDateTime start,
            Integer durationMinutes,
            UUID currentAppointmentId) {

        LocalDateTime end = start.plusMinutes(durationMinutes);

        List<Appointment> appointments =
                repository.findByPet_ExternalIdAndStatusIn(
                        petExternalId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                );

        for (Appointment a : appointments) {

            if (currentAppointmentId != null
                    && a.getExternalId().equals(currentAppointmentId)) {
                continue;
            }

            LocalDateTime aStart = a.getAppointmentDate();
            LocalDateTime aEnd = aStart.plusMinutes(a.getDurationMinutes());

            boolean overlap = start.isBefore(aEnd) && end.isAfter(aStart);

            if (overlap) {
                throw new BusinessRuleException(
                        "Pet already has an overlapping appointment."
                );
            }
        }
    }

    private void validatePastDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentTimeException("Cannot schedule in the past.");
        }
    }

    private void validateBranchSchedule(UUID branchId, LocalDateTime dateTime) {

        Branch branch = branchService.entityByExternalId(branchId);

        LocalTime time = dateTime.toLocalTime();

        LocalTime lastAvailableSlot =
                branch.getClosingTime().minusMinutes(30);

        if (time.isBefore(branch.getOpeningTime())
                || time.isAfter(lastAvailableSlot)) {

            throw new InvalidAppointmentTimeException(
                    "Branch is closed at that time."
            );
        }
    }

    private void validateHalfHourSlot(LocalDateTime dateTime) {

        int minute = dateTime.getMinute();

        if (minute != 0 && minute != 30) {
            throw new InvalidAppointmentTimeException(
                    "Appointments can only start on the hour or half hour."
            );
        }
    }

    private void validateVeterinarianSchedule(UUID veterinarianExternalId, LocalDateTime dateTime) {
        Veterinarian vet = veterinarianService.entityByExternalId(veterinarianExternalId);
        LocalTime time = dateTime.toLocalTime();

        if (vet.getWorkStartTime() == null || vet.getWorkEndTime() == null) {
            throw new BusinessRuleException("Veterinarian does not have working hours configured.");
        }

        LocalTime lastSlot = vet.getWorkEndTime().minusMinutes(30);

        if (time.isBefore(vet.getWorkStartTime()) || time.isAfter(lastSlot)) {
            throw new InvalidAppointmentTimeException("Appointment is outside veterinarian working hours.");
        }
    }

    @Transactional
    @Override
    public void confirmAppointment(UUID externalId) {

        Appointment appointment = entityByExternalId(externalId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Credentials credentials = getCurrentCredentials(authentication);
        User currentUser = credentials.getUser();

        boolean isVeterinarian = hasRole(authentication, Roles.ROLE_VETERINARIAN);
        boolean isReceptionist = hasRole(authentication, Roles.ROLE_RECEPTIONIST);

        boolean authorized = false;

        if (isVeterinarian) {
            Veterinarian assignedVeterinarian = appointment.getVeterinarian();

            authorized = assignedVeterinarian != null
                    && Boolean.TRUE.equals(assignedVeterinarian.getActive())
                    && assignedVeterinarian.getUser() != null
                    && Objects.equals(assignedVeterinarian.getUser().getId(), currentUser.getId());
        }

        if (!authorized && isReceptionist) {
            Receptionist receptionist = receptionistRepository.findByUser(currentUser).orElse(null);

            authorized = receptionist != null
                    && Boolean.TRUE.equals(receptionist.getActive())
                    && receptionist.getBranch() != null
                    && appointment.getBranch() != null
                    && Objects.equals(receptionist.getBranch().getId(), appointment.getBranch().getId());
        }

        if (!authorized) {
            throw new AccessDeniedException("You are not allowed to confirm this appointment.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING appointments can be confirmed.");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);

        repository.save(appointment);
    }

    @Override
    public List<LocalTime> getAvailableSlots(UUID veterinarianExternalId, UUID branchExternalId, LocalDate date) {

        Veterinarian vet = veterinarianService.entityByExternalId(veterinarianExternalId);
        Branch branch = branchService.entityByExternalId(branchExternalId);

        if (vet.getWorkStartTime() == null || vet.getWorkEndTime() == null) {
            throw new BusinessRuleException("Veterinarian does not have working hours configured.");
        }

        LocalTime effectiveStartTime = vet.getWorkStartTime().isAfter(branch.getOpeningTime())
                ? vet.getWorkStartTime()
                : branch.getOpeningTime();

        LocalTime effectiveEndTime = vet.getWorkEndTime().isBefore(branch.getClosingTime())
                ? vet.getWorkEndTime()
                : branch.getClosingTime();

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        List<LocalTime> takenSlots = repository
                .findByVeterinarian_ExternalIdAndStatusInAndAppointmentDateBetween(
                        veterinarianExternalId,
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED),
                        dayStart,
                        dayEnd)
                .stream()
                .map(a -> a.getAppointmentDate().toLocalTime())
                .toList();

        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime slot = effectiveStartTime;

        while (!slot.plusMinutes(30).isAfter(effectiveEndTime)) {
            if (!takenSlots.contains(slot)) {
                availableSlots.add(slot);
            }
            slot = slot.plusMinutes(30);
        }

        return availableSlots;
    }

    private Credentials getCurrentCredentials(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("Authentication required");
        }

        Credentials credentials = credentialsRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credentials",
                        "username",
                        authentication.getName()
                ));

        if (!credentials.isEnabled()
                || credentials.getUser() == null
                || credentials.getUser().getUserState() != UserState.ACTIVE) {
            throw new AccessDeniedException("User account is inactive or disabled.");
        }

        return credentials;
    }

    private boolean hasRole(Authentication authentication, Roles role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }

    private void authorizeUpdate(
            Appointment appointment,
            AppointmentUpdateDTO dto,
            Authentication authentication) {

        if (authentication != null && hasRole(authentication, Roles.ROLE_ADMIN)) {
            return;
        }

        Credentials credentials = getCurrentCredentials(authentication);
        User currentUser = credentials.getUser();

        if (hasRole(authentication, Roles.ROLE_RECEPTIONIST)) {
            authorizeReceptionistUpdate(appointment, dto, currentUser);
            return;
        }

        throw new AccessDeniedException("You are not allowed to update this appointment.");
    }

    private void authorizeReceptionistUpdate(
            Appointment appointment,
            AppointmentUpdateDTO dto,
            User currentUser) {

        Receptionist receptionist = receptionistRepository.findByUser(currentUser).orElse(null);

        if (receptionist == null
                || !Boolean.TRUE.equals(receptionist.getActive())
                || receptionist.getBranch() == null
                || appointment.getBranch() == null
                || !Objects.equals(receptionist.getBranch().getId(), appointment.getBranch().getId())) {
            throw new AccessDeniedException("You are not allowed to update this appointment.");
        }

        if (!Objects.equals(dto.branchExternalId(), appointment.getBranch().getExternalId())) {
            throw new AccessDeniedException("Receptionists cannot move appointments to another branch.");
        }

        if (appointment.getVeterinarian() == null
                || !Objects.equals(dto.veterinarianExternalId(), appointment.getVeterinarian().getExternalId())) {

            Veterinarian newVeterinarian = veterinarianService.entityByExternalId(dto.veterinarianExternalId());

            if (!Boolean.TRUE.equals(newVeterinarian.getActive())
                    || newVeterinarian.getBranch() == null
                    || !Objects.equals(newVeterinarian.getBranch().getId(), receptionist.getBranch().getId())) {
                throw new AccessDeniedException(
                        "Receptionists can only assign active veterinarians from their branch."
                );
            }
        }
    }

    private List<UUID> getClientPetExternalIds(String username) {
        Credentials credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Credentials", "username", username));

        User user = credentials.getUser();

        Owner owner = ownerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "user", user.getId()));

        List<OwnerByPet> ownerByPets = ownerByPetRepository.findByOwnerId(owner.getId());

        return ownerByPets.stream()
                .map(ownerByPet -> ownerByPet.getPet().getExternalId())
                .collect(Collectors.toList());
    }

    private List<UUID> getClientPetExternalIdsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals(Roles.ROLE_CLIENT.name()))) {
            return null;
        }

        return getClientPetExternalIds(authentication.getName());
    }

    private UUID getVeterinarianExternalIdForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals(Roles.ROLE_VETERINARIAN.name()))) {
            return null;
        }

        Credentials credentials = credentialsRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credentials",
                        "username",
                        authentication.getName()
                ));

        Veterinarian veterinarian = veterinarianRepository.findByUser(credentials.getUser())
                .orElse(null);

        return veterinarian != null ? veterinarian.getExternalId() : null;
    }

    private void validateClientPetOwnership(
            UUID petExternalId,
            String resourceName,
            UUID resourceExternalId) {

        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();

        if (clientPetExternalIds != null
                && !clientPetExternalIds.contains(petExternalId)) {
            throw new ResourceNotFoundException(resourceName, "externalId", resourceExternalId);
        }
    }

}