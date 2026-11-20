# NickMod (Fabric 1.21.11)

- `/nick <player> <nickname>` – set a nickname (supports `&` colour codes). OP only.
- `/nick <player> reset` – remove it.
- Non-ops cannot chat and cannot run ANY command (blocked in `CommandManagerMixin`).
- Nicks are saved in `config/nickmod_nicks.json`.

Build: `gradle build` (or add the Gradle wrapper via `gradle wrapper`), jar ends up in `build/libs/`.
Needs JDK 21 and Fabric API on the server.
