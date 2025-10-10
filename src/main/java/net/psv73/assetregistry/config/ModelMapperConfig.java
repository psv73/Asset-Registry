package net.psv73.assetregistry.config;

import net.psv73.assetregistry.entity.Asset;
import net.psv73.assetregistry.web.response.AssetResponseDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();

        mm.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)   // НИКАКОЙ автомагии
                .setAmbiguityIgnored(true);

        // InetAddress -> String
        Converter<InetAddress, String> ipToString =
                ctx -> ctx.getSource() == null ? null : ctx.getSource().getHostAddress();

        // Явная карта Asset -> AssetResponseDto
        TypeMap<Asset, AssetResponseDto> map = mm.createTypeMap(Asset.class, AssetResponseDto.class);
        map.addMappings(m -> {
            m.map(Asset::getId, AssetResponseDto::setId);
            m.map(Asset::getInventoryCode, AssetResponseDto::setInventoryCode);
            m.map(Asset::getSerialNumber,  AssetResponseDto::setSerialNumber);
            m.map(Asset::getHostname,      AssetResponseDto::setHostname);
            m.using(ipToString).map(Asset::getIp, AssetResponseDto::setIp);
            m.map(Asset::isDhcp,           AssetResponseDto::setDhcp);
            m.map(Asset::getPurchaseDate,  AssetResponseDto::setPurchaseDate);
            m.map(Asset::getParams,        AssetResponseDto::setParams);
            m.map(Asset::getNote,          AssetResponseDto::setNote);
            m.map(Asset::getCreateDatetime,AssetResponseDto::setCreateDatetime);
            m.map(Asset::getUpdateDatetime,AssetResponseDto::setUpdateDatetime);

            // Связи — только id, без инициализации прокси
            m.map(src -> src.getClient() != null ? src.getClient().getId() : null, AssetResponseDto::setClientId);
            m.map(src -> src.getModel()  != null ? src.getModel().getId()  : null, AssetResponseDto::setModelId);
            m.map(src -> src.getStatus() != null ? src.getStatus().getId() : null, AssetResponseDto::setStatusId);
            m.map(src -> src.getOs()     != null ? src.getOs().getId()     : null, AssetResponseDto::setOsId);
        });

        return mm;
    }
}
