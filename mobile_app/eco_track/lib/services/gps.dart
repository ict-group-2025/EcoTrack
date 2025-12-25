import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';

//Lay toa do
Future<bool> requestLocationPermission() async {
  // 1. Kiểm tra dịch vụ vị trí
  bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
  if (!serviceEnabled) {
    return false; // GPS đang tắt
  }

  // 2. Kiểm tra quyền
  LocationPermission permission = await Geolocator.checkPermission();

  if (permission == LocationPermission.denied) {
    permission = await Geolocator.requestPermission();
    if (permission == LocationPermission.denied) {
      return false;
    }
  }

  if (permission == LocationPermission.deniedForever) {
    // Người dùng đã chặn vĩnh viễn
    return false;
  }

  return true;
}
Future<Position> getCurrentLocation() async {
  return await Geolocator.getCurrentPosition(
    desiredAccuracy: LocationAccuracy.high,
  );
}

//Chuyen toa do thanh vi tri cu the
Future<String> getAddressFromLatLng(double latitude, double longitude) async {
  List<Placemark> placemarks = await placemarkFromCoordinates(
    latitude,
    longitude,
  );

  Placemark place = placemarks.first;

  return "${place.subAdministrativeArea}, "
      "${place.administrativeArea}, "
      "${place.country}";
}
