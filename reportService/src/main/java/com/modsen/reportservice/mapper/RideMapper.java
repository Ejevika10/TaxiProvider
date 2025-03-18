package com.modsen.reportservice.mapper;

import com.modsen.reportservice.dto.RatingResponseDto;
import com.modsen.reportservice.dto.RideResponseDto;
import com.modsen.reportservice.model.RideForReport;
import com.modsen.reportservice.model.RideState;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideMapper {

    @Mapping(target = "id", source = "ride.id")
    @Mapping(target = "rideState", source = "ride.rideState", qualifiedByName = "mapRideStateToString")
    @Mapping(target = "rideCost", source = "ride.rideCost", qualifiedByName = "mapRideCostToDouble")
    @Mapping(target = "rideDateTime", source = "ride.rideDateTime", qualifiedByName = "mapRideDateTimeToDate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RideForReport toRideForReport(RideResponseDto ride, RatingResponseDto rating);

    @Mapping(target = ".", ignore = true)
    default List<RideForReport> toRideForReportList(List<RideResponseDto> rideList, List<RatingResponseDto> ratingList){
        if(rideList == null || rideList.isEmpty())
            return List.of();

        Map<Long, RatingResponseDto> ratingMap = ratingList.stream()
                .collect(Collectors.toMap(RatingResponseDto::rideId, Function.identity()));

        return rideList.stream()
                .map(ride -> toRideForReport(ride, ratingMap.get(ride.id())))
                .collect(Collectors.toList());
    }



    @Named("mapRideStateToString")
    default String mapRideStateToString(RideState rideState) {
        return rideState.getState();
    }

    @Named("mapRideDateTimeToDate")
    default Date mapRideDateTimeToDate(LocalDateTime rideDateTime) {
        return Date.from(rideDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    @Named("mapRideCostToDouble")
    default Double mapRideCostToDouble(Integer rideCost) {
        return (double)rideCost/100;
    }
}
