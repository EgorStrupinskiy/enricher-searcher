# Enricher Service Microservice

The Enricher Service microservice is an essential component of the Microservices Music Metadata Enrichment System, responsible for enriching song metadata received from uploaded song files. It interacts with the File Service to obtain the file ID, downloads the file, and then leverages the Spotify API to fetch additional data about the track, such as the author, alternative track name, and duration.

## Overview

The Enricher Service microservice plays a pivotal role in the music metadata enrichment process. It processes song files, extracts metadata, and enhances it with supplementary details obtained from the Spotify API. Once enriched, the metadata is sent to the Song Service via a message queue for database storage and further accessibility.

## Functionality

- **File Processing**: The Enricher Service consumes song file IDs from the message queue and downloads the corresponding song files, which may be stored locally or in the Amazon S3 storage.

- **Metadata Enrichment**: Upon downloading the song file, the Enricher Service extracts essential metadata such as the song's title, artist, and duration. It then fetches additional information, such as alternative track names, from the Spotify API.

- **Message Queue Handling**: The Enricher Service interacts with Kafka Messaging Service, facilitating smooth communication between microservices.

## Dependencies

- **Spring Boot**: The microservice is built using the Spring Boot framework, providing a lightweight and efficient foundation.


- **Spotify API**: The Enricher Service leverages the Spotify API to fetch supplementary data about the song tracks.

## How to Use

The Enricher Service operates as an independent microservice and does not require direct user interaction. It automatically receives song file IDs from the message queue, downloads the corresponding files, and enriches their metadata with data from the Spotify API. The enriched metadata is then sent to the Song Service via another message queue.

## Contribution

Contributions to the Enricher Service or the entire Microservices Music Metadata Enrichment System are welcome. Developers can contribute by opening issues, submitting pull requests, or improving the documentation.