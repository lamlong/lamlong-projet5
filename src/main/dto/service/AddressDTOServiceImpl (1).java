package com.safetynet.alerts.dto.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safetynet.alerts.dto.AddressAdultChildDTO;
import com.safetynet.alerts.dto.AddressChildDTO;
import com.safetynet.alerts.dto.AddressPersonDTO;
import com.safetynet.alerts.dto.AddressPersonDTOPerson;
import com.safetynet.alerts.dto.AddressPersonDTOStationNumbers;
import com.safetynet.alerts.dto.AddressPersonEmailDTO;
import com.safetynet.alerts.model.Person;

import lombok.Setter;


@Service
public class AddressDTOServiceImpl implements AddressDTOService {
    @Autowired
	private ModelMapper modelMapper;
    
 	/**
	 * injected from AddressService
	 */
	@Setter
	private List<String> stationNumbers;
	
	@Override
	public List<AddressAdultChildDTO> addressChildrenToDTO(List<Person> addressChildren) {
		return addressChildren.stream().map(this::convertAddressChildrenToDTO).collect(Collectors.toList());
	}
	
	@Override
	public AddressAdultChildDTO convertAddressChildrenToDTO(Person child) {
		AddressChildDTO addressChildDTO = modelMapper.map(child, AddressChildDTO.class);
		addressChildDTO.setAdults(child.getAddress().getPersons().values().stream().filter(person -> person.getLastName().equals(child.getLastName())&&(person.getAge() > 18)).map(person -> modelMapper.map(person, AddressAdultChildDTO.class)).collect(Collectors.toList()));
		return addressChildDTO;
	}

	@Override
	public List<AddressPersonDTO> addressPersonsToDTO(List<Person> addressPersons) {
		List<AddressPersonDTO> addressPersonsDTO = addressPersons.stream().map(this::addressPersonToDTO).collect(Collectors.toList());
		addressPersonsDTO.add(new AddressPersonDTOStationNumbers(stationNumbers));
		return addressPersonsDTO;
	}

	@Override
	public AddressPersonDTO addressPersonToDTO(Person addressPerson) {
		modelMapper.typeMap(Person.class, AddressPersonDTOPerson.class).addMappings(mapper -> {
			mapper.map(src -> src.getMedicalrecord().getMedications(), AddressPersonDTOPerson::setMedications);
			mapper.map(src -> src.getMedicalrecord().getAllergies(), AddressPersonDTOPerson::setAllergies);
		});
		return modelMapper.map(addressPerson, AddressPersonDTOPerson.class);
	}
	
	@Override
	public List<AddressPersonEmailDTO> addressPersonEmailToDTO(List<Person> addressPersonEmail) {
		return addressPersonEmail.stream().map(person -> modelMapper.map(person, AddressPersonEmailDTO.class)).distinct().collect(Collectors.toList());
	}
}
