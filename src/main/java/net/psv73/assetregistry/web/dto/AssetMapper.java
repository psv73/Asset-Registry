package net.psv73.assetregistry.web.dto;

import net.psv73.assetregistry.entity.Asset;
import net.psv73.assetregistry.web.response.AssetResponseDto;

public final class AssetMapper {
    private AssetMapper(){}

    public static AssetResponseDto toResponse(Asset a) {
        AssetResponseDto dto = new AssetResponseDto();
        dto.setId(a.getId());
        dto.setClientId(a.getClient() != null ? a.getClient().getId() : null);
        dto.setModelId (a.getModel()  != null ? a.getModel().getId()  : null);
        dto.setStatusId(a.getStatus() != null ? a.getStatus().getId() : null);
        dto.setOsId    (a.getOs()     != null ? a.getOs().getId()     : null);
        dto.setInventoryCode(a.getInventoryCode());
        dto.setSerialNumber(a.getSerialNumber());
        dto.setHostname(a.getHostname());
        dto.setIp(a.getIp() != null ? a.getIp().getHostAddress() : null);
        dto.setDhcp(a.isDhcp());
        dto.setPurchaseDate(a.getPurchaseDate());
        dto.setParams(a.getParams());
        dto.setNote(a.getNote());
        dto.setCreateDatetime(a.getCreateDatetime());
        dto.setUpdateDatetime(a.getUpdateDatetime());
        return dto;
    }
}
