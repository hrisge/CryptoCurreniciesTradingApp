package com.cryptotrading.mapper;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.dto.ApplicationUserHoldingDTO;
import com.cryptotrading.entity.ApplicationUser;
import com.cryptotrading.entity.ApplicationUserHolding;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationUserHoldingMapper {
    ApplicationUserHolding dtoToEntity(final ApplicationUserHoldingDTO applicationUserHoldingDTO);

    ApplicationUserHoldingDTO entityToDTO(final ApplicationUserHolding applicationUserHolding);

    List<ApplicationUserHoldingDTO> entityListToDtoList(final List<ApplicationUserHolding> applicationUserHoldingList);

    List<ApplicationUserHolding> dtoListToEntityList(final List<ApplicationUserHoldingDTO> applicationUserHoldingDTOList);
}
