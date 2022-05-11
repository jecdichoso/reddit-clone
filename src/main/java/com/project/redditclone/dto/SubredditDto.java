package com.project.redditclone.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.AssociationOverride;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubredditDto {
    public Long id;
    private String name;
    private String description;
    private Integer numberOfPosts;

}
