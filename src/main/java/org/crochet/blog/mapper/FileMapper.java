package org.crochet.blog.mapper;

import org.crochet.blog.model.File;
import org.crochet.blog.payload.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    File toEntity(FileResponse fileResponse);

    Set<File> toEntities(Collection<FileResponse> fileResponses);

    FileResponse toResponse(File file);

    List<FileResponse> toResponses(Collection<File> files);
}
