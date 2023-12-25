package dev.idqnutlikeit.clans.storage;

import com.zaxxer.hikari.HikariDataSource;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public abstract class DbStorage implements Storage {
  private final HikariDataSource db;
}
