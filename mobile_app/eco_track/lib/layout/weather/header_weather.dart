import 'package:flutter/material.dart';

class HeaderWeather  extends StatelessWidget {
  final String locationName;
  const HeaderWeather ({super.key, required this.locationName});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        const Icon(Icons.menu, color: Colors.white),
        Expanded(
          child: Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.location_on, color: Colors.white, size: 20),
              const SizedBox(width: 5),
              Flexible(
                child: Text(
                  locationName,
                  style: const TextStyle(color: Colors.white, fontSize: 16),
                  overflow: TextOverflow.ellipsis,
                  maxLines: 1,
                ),
              ),
            ],
          ),
        ),
        const Icon(Icons.more_vert, color: Colors.white),
      ],
    );
  }
}
