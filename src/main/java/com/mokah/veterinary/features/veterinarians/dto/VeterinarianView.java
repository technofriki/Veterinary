package com.mokah.veterinary.features.veterinarians.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public interface VeterinarianView {
    UUID externalId();
    String firstName();
    String lastName();
    LocalTime workStartTime();
    LocalTime workEndTime();
    Set<DayOfWeek> workDays();
}
