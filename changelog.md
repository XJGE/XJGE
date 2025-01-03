# Changelog

All notable changes to this project will be documented in this file. The format of this file follows that specified by [Keep a Changelog](https://keepachangelog.com/en/1.0.0/). This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.1.0] - 2025-01-03

### Changed
- Deprecated render() method in Entity that only used a single glProgram, this will be removed in a future version.
- Entity collection in Scene now uses integers as keys instead of Strings.
- 

## [3.0.0] - 2024-06-04

### Added
- Maximum filesize on log files so they can roll over instead of missing details.
- New abstract processMouseInput() method to Widget superclass that provides input information from a users mouse. Like processKeyInput(), this only listens on viewport 0.
- New setInputDevice() method to Puppet class. This effectively flips input on its head an prevents puppets from being controlled by more than one input device at a time.
- New addObserver() method to XJGE class. This can be used to notify the implementing game project of engine state changes.

### Changed
- Altered log format to make them easier to read and search.
- setSplitScreen() method in Widget class to relocate().
- Input.pollInput() method to the more generic update() to reflect its altered usage.
- Fixed fatal bug in Font class that would fail to load the default engine font if another file couldn't be read/found.
- Values of device IDs assigned in Input class. KEY_MOUSE_COMBO is now using index 16, and the AI_GAMEPAD values now range from 17 to 32.
- Fixed bug that would scroll command terminal input even if the edge of the window wasn't reached.

### Removed
- Mechanisms that allowed input devices to maintain a list of the puppet objects they'd used.
- getDevicePuppet(), setDevicePuppet(), and bindPreviousPuppet() from the Input class.
- Missing gamepad event and widget since their functionality has been moved to the engine observer.
- addDisConWidget() and missingGamepadInitialized() methods from input class.
- revertEnabledState() method from input class. This was only ever used internally to renable the keyboard after using noclip or the command terminal.

## [2.4.2] - 2024-02-10

### Added
- New setResolution() method to the XJGE class that can be used to change the resolutions of framebuffers at runtime.
- New getButtonID() method to the Command class that returns a number used by GLFW to identify the components of an input device.
- New containsWidget() method to XJGE class to test whether a viewport contains a specified widget.
- Various new methods to Icon class to give developers greater control over how they appear on screen.
- New lerp() utility function to XJGE class.
- New reset() method to StopWatch and SpriteAnimation classes.

### Changed
- Fixed issue that caused sprites to display incorrectly due to the value of texCoords only changing during the update cycles where sync was enabled.
- Filenames of various assets used by the engine to reflect new conventions.
- Font used by engine utilities to Source Code Pro.
- Fixed issue with Color(r, g, b) constructor where only the value of 'r' would be used.
- Minor changes made to default color palette to be more consistent across different platforms.

### Removed
- Restriction that required gamepads to be reconnected to resolve missing input device event, developers can now enable this feature optionally depending on the needs of their project.

## [2.1.9] - 2022-06-19

### Added
- Observable object in the Game class which notifies observers whenever the scene is changed.
- New clearWidgets() method in XJGE class can be used to quickly remove all widgets from a viewport.
- The engines version number has been included in the system information displayed during startup.
- New "resolveEvent" field to Widget class that will need to be changed to true before a missing gamepad event can be resolved.
- The logger now outputs a message anytime a widget object is added or removed from a viewport.
- New VirtualGamepad class that an AI can use to interface with controllable game objects much in the same way a player would.
- New setVirtualGamepadInput() method in the input class can be used to supply input values to the individual components of virtual gamepads.
- New PostProcessShader object can apply custom post-processing effects to the framebuffer texture of a viewport using XJGE.changeFramebufferFilter()
- New setOpacity() method to Icon class.
- Included missing javadoc to some methods in the Viewport class.

### Changed
- Fixed issue with the SpriteAnimation class where setting the speed to zero would cause an ArithmeticException.
- Fixed bug where the command terminal would output error even if a command completed successfully.
- Made static clampValue() method in XJGE class public since it's so darn useful.
- The Widget class now includes a destory() method to free resources after it's removed from a viewport.
- Modified default fragment shader so bitmap font texture data can influence color output.
- Fixed issue where the widget used during a missing gamepad event wouldn't be removed after the event was resolved.
- Included extra parameter to disableAllExcept() method that can also be used to disable the virtual input devices of AI controlled entities.
- Fixed bug where using values outside of GLFW_JOYSTICK_1-4 in Input.getDevicePresent() would cause an ArrayIndexOutOfBoundsException.
- Fixed bug inside Input.disableAllExcept() that would not renable some devices even after a missing gamepad event was resolved.
- Update() methods of SpriteAnimation class now include a "sync" parameter that can be used to synchronize animations with the game loop.
- UI widgets are now added and removed asynchronously after doing so would before would cause ConcurrentModificationExceptions.
- Refactored texture stage of viewport render process to allow for custom functionality using post-process shader objects.

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

[2.0.0]: https://github.com/XJGE/XJGE/releases/tag/2.0.0
[2.1.9]: https://github.com/XJGE/XJGE/releases/tag/2.1.9
[2.4.2]: https://github.com/XJGE/XJGE/releases/tag/2.4.2
[3.0.0]: https://github.com/XJGE/XJGE/releases/tag/3.0.0