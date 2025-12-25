import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';

class LocationProvider extends ChangeNotifier {
  Position? _position;
  String? _locationName;

  Position? get position => _position;
  String? get locationName => _locationName;

  // Set vị trí và reverse geocoding
  Future<void> setPosition(Position pos) async {
    _position = pos;

    try {
      final placemarks = await placemarkFromCoordinates(
        pos.latitude,
        pos.longitude,
      );
      final place = placemarks.first;
      _locationName =
          "${place.subAdministrativeArea}, ${place.administrativeArea}";
    } catch (e) {
      _locationName = "${pos.latitude}, ${pos.longitude}";
    }

    notifyListeners();
  }
}
