/*
 * PilotLog
 *
 * Copyright © 2018 Richard Senior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flightgear.pilotlog.service;

import org.flightgear.pilotlog.domain.Airport;
import org.flightgear.pilotlog.domain.AirportRepository;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;
    private final PageableUtil pageableUtil;

    public AirportService(AirportRepository airportRepository,
        FlightRepository flightRepository,
        PageableUtil pageableUtil
    ) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
        this.pageableUtil = pageableUtil;
    }

    @Transactional
    public void updateSummary(String... airports) {
        for (String icao : airports) {
            int departures = flightRepository.countByOrigin(icao);
            int arrivals = flightRepository.countByDestination(icao);

            Date last = null;
            if (departures > 0) {
                Flight f = flightRepository.findFirstByOriginOrderByStartTimeDesc(icao);
                if (f != null) {
                    last = f.getStartTime();
                }
            }
            if (arrivals > 0) {
                Flight f = flightRepository.findFirstByDestinationOrderByStartTimeDesc(icao);
                if (f != null && (last == null || f.getStartTime().compareTo(last) > 0)) {
                    last = f.getStartTime();
                }
            }

            Optional<Airport> optional = airportRepository.findById(icao);
            if (optional.isPresent()) {
                Airport airport = optional.get();
                airport.setArrivals(arrivals);
                airport.setDepartures(departures);
                airport.setLast(last);
                if (airport.getMovements() == 0) {
                    airportRepository.delete(airport);
                } else {
                    airportRepository.save(airport);
                }
            } else {
                Airport airport = new Airport(icao, arrivals, departures, last);
                airportRepository.save(airport);
            }
        }
    }

    // Query methods

    @Transactional(readOnly = true)
    public List<Airport> findAllAirports() {
        return airportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Airport> findAllAirports(Pageable pageable) {
        pageable = pageableUtil.adjustPageable(pageable, "icao", "icao");
        return airportRepository.findAll(pageable);
    }

}