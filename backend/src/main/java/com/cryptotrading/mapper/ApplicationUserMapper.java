package com.cryptotrading.mapper;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.entity.ApplicationUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationUserMapper {
    ApplicationUser dtoToEntity(final ApplicationUserDTO applicationUserDTO);

    ApplicationUserDTO entityToDto(final ApplicationUser applicationUser);

    List<ApplicationUser> dtoListToEntityList(final List<ApplicationUserDTO> applicationUserDTOList);

    List<ApplicationUserDTO> entityListToDTOList(final List<ApplicationUser> applicationUserList);
}
