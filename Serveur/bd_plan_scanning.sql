-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Client :  127.0.0.1
-- Généré le :  Ven 08 Janvier 2016 à 19:34
-- Version du serveur :  5.6.17
-- Version de PHP :  5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `bd_plan_scanning`
--

-- --------------------------------------------------------

--
-- Structure de la table `beacon`
--

CREATE TABLE IF NOT EXISTS `beacon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `posMX` int(11) DEFAULT NULL,
  `posMY` int(11) DEFAULT NULL,
  `minorId` int(11) DEFAULT NULL,
  `majorId` int(11) DEFAULT NULL,
  `MAC` varchar(40) DEFAULT NULL,
  `id_plan` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_plan_beacon_id` (`id_plan`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=53 ;

--
-- Contenu de la table `beacon`
--

INSERT INTO `beacon` (`id`, `x`, `y`, `posMX`, `posMY`, `minorId`, `majorId`, `MAC`, `id_plan`) VALUES
(49, 59, 68, 0, 0, 1, 21777, '', 28),
(50, 140, 412, 0, 1, 1, 105, '', 28),
(51, 536, 67, 3, 0, 1, 22236, '', 28),
(52, 611, 400, 3, 1, 1, 22235, '', 28);

-- --------------------------------------------------------

--
-- Structure de la table `plan`
--

CREATE TABLE IF NOT EXISTS `plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `img` varchar(255) NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `longM` float NOT NULL,
  `largM` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=29 ;

--
-- Contenu de la table `plan`
--

INSERT INTO `plan` (`id`, `img`, `width`, `height`, `longM`, `largM`) VALUES
(28, 'upload_plan/plan_chambre.png', 656, 949, 3, 4);

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `posX` int(11) DEFAULT NULL,
  `posY` int(11) DEFAULT NULL,
  `lastUpdate` timestamp NULL DEFAULT NULL,
  `id_plan` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan_id` (`id_plan`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=66 ;

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `beacon`
--
ALTER TABLE `beacon`
  ADD CONSTRAINT `fk_plan_beacon_id` FOREIGN KEY (`id_plan`) REFERENCES `plan` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`id_plan`) REFERENCES `plan` (`id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
