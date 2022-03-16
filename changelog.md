# Changelog

All notable changes to this project will be documented in this file. The format of this file follows that specified by [Keep a Changelog](https://keepachangelog.com/en/1.0.0/). This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).



## [2.0.0] - 2022-03-15

### Added
- Increased stability through continued error testing.
- New StopWatch class can be used as a lightweight timing mechanism for simple gameplay systems.
- The ability to generate a separate log file containing only OpenGL state changes.
- New showCommands command will display every command available to the terminal including user defined commands.
- The model matrix of the Skybox can now be used to make it exhibit all sorts of fun effects such as rotation.
- Completely overhauled how input devices are managed with the addition of the static Input class.
- Pressing F3 will expose the positions of all light objects within the scene.
- Pressing F4 will produce a sound. This is particularly useful when testing external audio devices.
- The ability to cast dynamic shadows in real-time with the ShadowMap class.
- New bloom effect which can be altered through the Game class.
- New Hardware class that works in the same fashion as Input albeit for other peripheral devices such as monitors and speakers.
- Entities now include a renderShadow() method that is provided with a special shader program designed to produce shadow information.
- Scenes also include the aforementioned renderShadow() method but with the intention of grouping entities/objects that wish to have their own shadow methods called.
- Uninitialized Widget object in the Input class that will display when a controller is suddenly unplugged. This effectively lets the implementation decide how to resolve the issue/style the error message.

### Changed
- Classes are grouped to fewer packages to take advantage of package-privacy. This greatly reduced the number of leaking implementation details.
- Debugging utilities including the command terminal now scale independently of the games internal resolution.
- The debug information displayed when F1 is pressed now includes system hardware and controller states in addition to runtime information.
- The command terminal is now opened using SHIFT + F1.
- The help command is now substantially more helpful.
- Increased timer accuracy by utilizing the current game tick from which the timer is initially activated.
- The lighting of the default shaders now uses a "Blinn-Phong" type hybrid model.
- Supplied Model class with overstuffed variants of its existing methods so custom shadow functionality may be defined by users if they please.
- The GLCapabilites object was renamed to GLCaps to avoid confusion with the LWJGL class of the same name.
- Kept component-based UI system but greatly altered how some features such as text rendering are achieved.

### Removed
- "Default" assets which largely went unused anyway. Now only the fallback texture and sound are provided.
- The showInputInfo command since this information is displayed along with other debug information when F1 is pressed.
- LightSource objects as they were almost entirely redundant. Light objects may now be created freely.

[2.0.0]: https://github.com/XJGE/XJGE-2/releases/tag/2.0.0
