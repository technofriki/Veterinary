package com.mokah.veterinary.features.users.dto;

import java.util.UUID;

public record UserPublicResponse(UUID externalId, String firstName, String lastName) {}
