-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: organigrammadb
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `employee_roles`
--

DROP TABLE IF EXISTS `employee_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee_roles` (
  `employee_name` varchar(50) NOT NULL,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`employee_name`,`role_name`),
  KEY `role_name` (`role_name`),
  CONSTRAINT `employee_roles_ibfk_1` FOREIGN KEY (`employee_name`) REFERENCES `employees` (`name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `employee_roles_ibfk_2` FOREIGN KEY (`role_name`) REFERENCES `roles` (`name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee_roles`
--

LOCK TABLES `employee_roles` WRITE;
/*!40000 ALTER TABLE `employee_roles` DISABLE KEYS */;
INSERT INTO `employee_roles` VALUES ('Marta D.','CASSA'),('Marta F.','CASSA'),('Max','CEO'),('Giuseppe','CONSIGLIERE'),('Ada','CUSTOMER'),('Carlo','MANAGER'),('Pino','ONLINE'),('Giovanni','PRESIDENTE'),('Max','PRESIDENTE'),('Marco','RICERCATORE'),('Antonio','RULLO');
/*!40000 ALTER TABLE `employee_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `name` varchar(50) NOT NULL,
  `workunit_name` varchar(50) NOT NULL,
  PRIMARY KEY (`name`,`workunit_name`),
  KEY `workunit_name` (`workunit_name`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`workunit_name`) REFERENCES `work_units` (`name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
INSERT INTO `employees` VALUES ('Marta D.','Acquisti'),('Marta F.','Acquisti'),('Carlo','Area vendite'),('Giovanni','Comitato tecnico'),('Giuseppe','Comitato tecnico'),('Max','Consiglio'),('Ada','Customer care'),('Antonio','Produzione'),('Marco','Ricerca e Sviluppo'),('Pino','Vendite Online');
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `name` varchar(50) NOT NULL,
  `workunit_name` varchar(50) NOT NULL,
  PRIMARY KEY (`name`,`workunit_name`),
  KEY `roles_ibfk_1` (`workunit_name`),
  CONSTRAINT `roles_ibfk_1` FOREIGN KEY (`workunit_name`) REFERENCES `work_units` (`name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES ('CASSA','Acquisti'),('MANAGER','Area vendite'),('CONSIGLIERE','Comitato tecnico'),('PRESIDENTE','Comitato tecnico'),('CEO','Consiglio'),('PRESIDENTE','Consiglio'),('CUSTOMER','Customer care'),('RULLO','Produzione'),('RICERCATORE','Ricerca e Sviluppo'),('ONLINE','Vendite Online');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_units`
--

DROP TABLE IF EXISTS `work_units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_units` (
  `name` varchar(50) NOT NULL,
  `parent_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_units`
--

LOCK TABLES `work_units` WRITE;
/*!40000 ALTER TABLE `work_units` DISABLE KEYS */;
INSERT INTO `work_units` VALUES ('Acquisti','root'),('Area vendite','root'),('Comitato tecnico','root'),('Consiglio','root'),('Customer care','Area vendite'),('Produzione','root'),('Ricerca e Sviluppo','Comitato tecnico'),('Vendite Online','Area vendite');
/*!40000 ALTER TABLE `work_units` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-02 16:19:16
