package com.modive.userservice.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalCarDto {
    private Long total;
    private Long present;
    private Long prior;

    public TotalCarDto(Number total, Number present, Number prior) {
        this.total = total != null ? total.longValue() : 0L;
        this.present = present != null ? present.longValue() : 0L;
        this.prior = prior != null ? prior.longValue() : 0L;
    }
}
