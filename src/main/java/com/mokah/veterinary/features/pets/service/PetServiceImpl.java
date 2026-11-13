package com.mokah.veterinary.features.pets.service;

import com.mokah.veterinary.common.exception.BusinessRuleException;
import com.mokah.veterinary.common.exception.InvalidDateException;
import com.mokah.veterinary.common.exception.ResourceNotFoundException;
import com.mokah.veterinary.features.animaltypes.mapper.AnimalTypeMapper;
import com.mokah.veterinary.features.animaltypes.model.AnimalType;
import com.mokah.veterinary.features.animaltypes.service.AnimalTypeService;
import com.mokah.veterinary.features.appointments.model.AppointmentStatus;
import com.mokah.veterinary.features.appointments.repository.AppointmentRepository;
import com.mokah.veterinary.features.breed.mapper.BreedMapper;
import com.mokah.veterinary.features.breed.model.Breed;
import com.mokah.veterinary.features.breed.service.BreedService;
import com.mokah.veterinary.features.owners.model.Owner;
import com.mokah.veterinary.features.owners.repository.OwnerRepository;
import com.mokah.veterinary.features.ownersbypets.model.OwnerByPet;
import com.mokah.veterinary.features.ownersbypets.repository.OwnerByPetRepository;
import com.mokah.veterinary.features.pets.dto.PetRequest;
import com.mokah.veterinary.features.pets.dto.PetResponse;
import com.mokah.veterinary.features.pets.mapper.PetMapper;
import com.mokah.veterinary.features.pets.model.Pet;
import com.mokah.veterinary.features.pets.repository.PetRepository;
import com.mokah.veterinary.features.users.model.User;
import com.mokah.veterinary.features.users.repository.UserRepository;
import com.mokah.veterinary.features.visits.dto.VisitResponse;
import com.mokah.veterinary.features.visits.mapper.VisitMapper;
import com.mokah.veterinary.features.visits.model.Visit;
import com.mokah.veterinary.features.visits.repository.VisitRepository;
import com.mokah.veterinary.security.enums.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

    private final PetRepository repository;
    private final PetMapper mapper;
    private final AnimalTypeService animalTypeService;
    private final AnimalTypeMapper animalTypeMapper;
    private final BreedService breedService;
    private final BreedMapper breedMapper;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerByPetRepository ownerByPetRepository;
    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    public PetResponse toResponse(Pet pet) {

        Visit lastVisit = visitRepository
                .findTop1ByPet_ExternalIdOrderByVisitDateDesc(pet.getExternalId())
                .orElse(null);

        Long visitsCount =
                visitRepository.countByPet_ExternalId(pet.getExternalId());

        LocalDate lastVisitDate = (lastVisit == null)
                ? null
                : lastVisit.getVisitDate().toLocalDate();

        Long activeAppointments =
                appointmentRepository.countByPet_ExternalIdAndStatusIn(
                        pet.getExternalId(),
                        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
                );

        return new PetResponse(
                pet.getExternalId(),
                pet.getName(),
                pet.getBirthDate(),
                pet.getActive(),
                pet.getColor(),
                animalTypeMapper.toResponse(pet.getAnimalType()),
                breedMapper.toResponse(pet.getBreed()),
                visitsCount,
                lastVisitDate,
                activeAppointments
        );
    }

    @Override
    public PetResponse create(PetRequest dto) {

        if (dto.birthDate() != null && dto.birthDate().isAfter(LocalDate.now())) {
            throw new InvalidDateException("Birth date can not be after now");
        }

        AnimalType animalType = animalTypeService.entityByExternalId(dto.animalTypeId());

        Breed breed = breedService.entityByExternalId(dto.breedId());
        if (!breed.getAnimalType().getExternalId().equals(animalType.getExternalId())) {
            throw new BusinessRuleException("Breed does not match the selected AnimalType");
        }

        Pet entity = Pet.builder()
                .name(dto.name())
                .birthDate(dto.birthDate())
                .active(true)
                .color(dto.color())
                .animalType(animalType)
                .breed(breed)
                .build();

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public List<PetResponse> findAll() {
        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();

        if (clientPetExternalIds != null) {
            return mapper.toResponseList(repository.findAll().stream()
                    .filter(pet -> clientPetExternalIds.contains(pet.getExternalId()))
                    .collect(Collectors.toList()));
        }

        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public Pet entityByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "externalId", externalId));
    }

    @Override
    public PetResponse findByExternalId(UUID externalId) {
        Pet pet = entityByExternalId(externalId);

        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();
        if (clientPetExternalIds != null && !clientPetExternalIds.contains(pet.getExternalId())) {
            throw new ResourceNotFoundException("Pet", "externalId", externalId);
        }

        return mapper.toResponse(pet);
    }

    @Override
    public PetResponse findByName(String name) {
        Pet entity = repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", "name", name));

        List<UUID> clientPetExternalIds = getClientPetExternalIdsForCurrentUser();
        if (clientPetExternalIds != null && !clientPetExternalIds.contains(entity.getExternalId())) {
            throw new ResourceNotFoundException("Pet", "name", name);
        }

        return mapper.toResponse(entity);
    }

    @Override
    public PetResponse update(UUID externalId, PetRequest dto) {

        Pet entity = entityByExternalId(externalId);

        if (dto.birthDate() != null) {

            if (dto.birthDate().isAfter(LocalDate.now())) {
                throw new InvalidDateException("Birth date can not be after now");
            }

            entity.setBirthDate(dto.birthDate());
        }

        entity.setColor(dto.color());
        entity.setName(dto.name());

        AnimalType animalType = animalTypeService.entityByExternalId(dto.animalTypeId());
        entity.setAnimalType(animalType);

        Breed breed = breedService.entityByExternalId(dto.breedId());
        if (!breed.getAnimalType().getExternalId().equals(animalType.getExternalId())) {
            throw new BusinessRuleException("Breed does not match the selected AnimalType");
        }
        entity.setBreed(breed);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {
        Pet pet = entityByExternalId(externalId);
        if (!pet.getActive()) {
            throw new BusinessRuleException("The selected pet is no longer active in the system.");
        }
        pet.setActive(false);
        repository.save(pet);
    }

    @Override
    public List<PetResponse> findPetsByAuthenticatedUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Owner owner = ownerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "user", user.getId()));

        List<OwnerByPet> ownerByPets = ownerByPetRepository.findByOwnerId(owner.getId());

        return ownerByPets.stream()
                .map(OwnerByPet::getPet)
                .filter(pet -> pet.getActive())
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<VisitResponse> getHistory(UUID petExternalId) {

        return visitMapper.toResponseList(
                visitRepository.findByPet_ExternalIdOrderByVisitDateDesc(petExternalId)
        );
    }

    private List<UUID> getClientPetExternalIdsForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals(Roles.ROLE_CLIENT.name()))) {
            return null;
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        Owner owner = ownerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "user", user.getId()));

        List<OwnerByPet> ownerByPets = ownerByPetRepository.findByOwnerId(owner.getId());

        return ownerByPets.stream()
                .map(ownerByPet -> ownerByPet.getPet().getExternalId())
                .collect(Collectors.toList());
    }
}