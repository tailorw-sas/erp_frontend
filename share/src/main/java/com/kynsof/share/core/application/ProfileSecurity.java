package com.kynsof.share.core.application;

import java.util.Set;

public record ProfileSecurity(Long agencyCode, Set<String> access) {
}
