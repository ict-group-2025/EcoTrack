import 'package:flutter/material.dart';

class HeaderWeather extends StatelessWidget {
  final String locationName;
  const HeaderWeather({super.key, required this.locationName});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [const Icon(Icons.search, color: Colors.white)],
        ),

        Row(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.location_on, color: Colors.white, size: 20),
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
      ],
    );
  }
}
