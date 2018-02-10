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

package org.flightgear.pilotlog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * Repository of flights.
 *
 * @author Richard Senior
 */
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    /**
     * Finds flights with a given status.
     *
     * @param status   the flight status
     * @param pageable the page context object
     * @return a set of flights with the given status
     * @see FlightStatus
     */
    Page<Flight> findByStatus(FlightStatus status, Pageable pageable);

    /**
     * Finds flights with a given status.
     *
     * @param status the flight status
     * @return a set of flights with the given status
     * @see FlightStatus
     */
    Set<Flight> findByStatus(FlightStatus status);

}
