package com.ganzib.repository;

import com.ganzib.model.domain.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 *     图库持久层
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2018/2/26
 */
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
}
