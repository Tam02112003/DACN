package DACN.DACN.services;

import DACN.DACN.entity.Size;
import DACN.DACN.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeService {
    @Autowired
    private SizeRepository sizeRepository;

    public List<Size> getAllSizes() {
        return sizeRepository.findAll();
    }

    public Size getSizeById(Long id) {
        return sizeRepository.findById(id).orElse(null);
    }

    public List<Size> getSizesByCategory(Long categoryId) {
        return sizeRepository.findByCategoryId(categoryId);
    }

    public Size saveSize(Size size) {
        return sizeRepository.save(size);
    }

    public void deleteSize(Long id) {
        sizeRepository.deleteById(id);
    }
}
