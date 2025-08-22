# Aşama 1: Build Ortamı
# Maven ve Java 24 içeren resmi bir build imajı kullan
FROM maven:3.9-eclipse-temurin-24 AS builder

# Çalışma dizinini ayarla
WORKDIR /app

# Önce sadece pom.xml'i kopyala ki bağımlılıklar katmanı cache'lensin
COPY project_management/pom.xml .

# Bağımlılıkları indir
RUN mvn dependency:go-offline

# Geri kalan tüm proje kodunu kopyala
COPY project_management/. .

# Projeyi paketle (testleri atla)
RUN mvn package -DskipTests

# Aşama 2: Çalıştırma Ortamı
# Sadece Java Runtime içeren hafif bir imaj kullan
FROM eclipse-temurin:24-jre

# Çalışma dizinini ayarla
WORKDIR /app

# Builder aşamasında oluşturulan JAR dosyasını kopyala
COPY --from=builder /app/target/projeyonetimsistemi-0.0.1-SNAPSHOT.jar app.jar

# Uygulamanın çalışacağı portu belirt
EXPOSE 8081

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]