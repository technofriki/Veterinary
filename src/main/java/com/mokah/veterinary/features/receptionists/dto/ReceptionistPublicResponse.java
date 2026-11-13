package com.mokah.veterinary.features.receptionists.dto;

import java.util.UUID;

public record ReceptionistPublicResponse(UUID externalId, String firstName, String lastName) {}
