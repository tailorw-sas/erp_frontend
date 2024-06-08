package com.kynsoft.gateway.domain.dto;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;

@Value
@RequiredArgsConstructor
@Builder
public class RouteDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 7885373191295584329L;
	String name;
	String path;
	URI uri;
}
