
package com.kynsof.share.core.application.payment.domain.placeToPlay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsResponse {
    private List<TransactionsClient> entities;
    private Pagination pagination;
}
