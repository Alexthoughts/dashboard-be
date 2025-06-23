package dashboard.services;

import dashboard.repository.HolidayRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;

    public List getHolidayList() {
        return holidayRepository.findAll();
    }
}
