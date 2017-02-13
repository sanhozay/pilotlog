/*
 * PilotLog
 *
 * Copyright (c) 2017 Richard Senior
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

package org.flightgear.pilotlog.web;

import java.util.List;

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.service.FlightService;
import org.flightgear.pilotlog.service.exceptions.FlightNotFoundException;
import org.flightgear.pilotlog.service.exceptions.InvalidFlightStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * Web service controller for PilotLog.
 *
 * @author Richard Senior
 */
@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired(required = true)
    private FlightService service;

    @GetMapping(path = "departure", produces = MediaType.TEXT_XML_VALUE)
    public Flight departure(
        @RequestParam("callsign") String callsign,
        @RequestParam("aircraft") String aircraft,
        @RequestParam("airport") String airport,
        @RequestParam("fuel") float startFuel,
        @RequestParam("odometer") float startOdometer) {
        return service.beginFlight(callsign, aircraft, airport, startFuel, startOdometer);
    }

    @GetMapping(path = "arrival", produces = MediaType.TEXT_XML_VALUE)
    public Flight arrival(
        @RequestParam("id") int id,
        @RequestParam("airport") String airport,
        @RequestParam("fuel") float endFuel,
        @RequestParam("odometer") float endOdometer)
        throws FlightNotFoundException, InvalidFlightStatusException {
        return service.endFlight(id, airport, endFuel, endOdometer);
    }

    @GetMapping(path = "invalidate", produces = MediaType.TEXT_XML_VALUE)
    public Flight invalidate(@RequestParam("id") int id)
        throws FlightNotFoundException, InvalidFlightStatusException {
        return service.invalidateFlight(id);
    }

    // Additional endpoints, over and above those created by Spring Data REST

    @GetMapping(path = "flights.xml", produces = {MediaType.TEXT_XML_VALUE,
        MediaType.APPLICATION_XML_VALUE})
    public List<Flight> flights() {
        return service.findAllFlights();
    }

    @GetMapping(path = "flights.csv", produces = {"text/csv"})
    public String flightsCSV() throws JsonProcessingException {
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(Flight.class).withHeader();
        return mapper.writer(schema).writeValueAsString(service.findAllFlights());
    }

}
