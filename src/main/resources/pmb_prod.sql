-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : ven. 30 août 2024 à 13:09
-- Version du serveur : 8.2.0
-- Version de PHP : 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `pmb_prod`
--

-- --------------------------------------------------------

--
-- Structure de la table `transactions`
--

DROP TABLE IF EXISTS `transactions`;
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `receiver_id` int DEFAULT NULL,
  `sender_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5nn8ird7idyxyxki68gox2wbx` (`receiver_id`),
  KEY `FK3ly4r8r6ubt0blftudix2httv` (`sender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `transactions`
--

INSERT INTO `transactions` (`id`, `amount`, `description`, `receiver_id`, `sender_id`) VALUES
(1, 10, 'Cadeau', 1, 2);

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `balance` double NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `balance`, `email`, `password`, `username`) VALUES
(1, 110, 'Charlie@email.com', '$2a$10$AODcDp7d3zSfFr/ytv83munUof0jx5F6p.t2JAq07tnYB9NHzqiSm', 'Charlie'),
(2, 90, 'Max@email.com', '$2a$10$nApX.EElhc5zj1xWtlQCOuMi49khIQE/S8dPk8UvEpE1yikh.7pyK', 'Max'),
(3, 100, 'Noe@email.com', '$2a$10$p1vxhxB8lVlwGBMa6iO/f.PlCMa9TAozhOevm9/peqvXbzEld28.S', 'Noé');

-- --------------------------------------------------------

--
-- Structure de la table `users_connections`
--

DROP TABLE IF EXISTS `users_connections`;
CREATE TABLE IF NOT EXISTS `users_connections` (
  `users_id` int NOT NULL,
  `connections_id` int NOT NULL,
  PRIMARY KEY (`users_id`,`connections_id`),
  KEY `FKe3bfhd5qe7ci5cy2rjghcvm2q` (`connections_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `users_connections`
--

INSERT INTO `users_connections` (`users_id`, `connections_id`) VALUES
(2, 1);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `FK3ly4r8r6ubt0blftudix2httv` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK5nn8ird7idyxyxki68gox2wbx` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`);

--
-- Contraintes pour la table `users_connections`
--
ALTER TABLE `users_connections`
  ADD CONSTRAINT `FKe3bfhd5qe7ci5cy2rjghcvm2q` FOREIGN KEY (`connections_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKoy6bqk6mu01x2ugabmnfm2gk7` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
