/**
 * Contains various classes, objects, and utilities many of which are essential 
 * to the function of the engine. These are listed in greater detail below:
 * <p>
 * <h2>Core Classes:</h2>
 * </p>
 * <table border="1">
 * <tr><td><b>Name</b></td><td><b>Description</b></td></tr>
 * <tr><td>{@linkplain Hardware}</td><td>Manages the state of various 
 *         peripheral devices including those used for audio output and 
 *         visual display.</td></tr>
 * <tr><td>{@linkplain Input}</td><td>Provides a single point of access through 
 *         which the state of peripheral input devices may be 
 *         managed.</td></tr>
 * <tr><td>{@linkplain Monitor}</td><td>Represents a visual display device such 
 *         as a computer monitor or television.</td></tr>
 * <tr><td>{@linkplain Viewport}</td><td>Represents a rectangular region of the 
 *         game window through which the perspective of a scene and its 
 *         rendered objects may be viewed.</td></tr>
 * <tr><td>{@linkplain Window}</td><td>Provides a point of access which can be 
 *         used to alter the properties of the game window at runtime.</td></tr>
 * </table>
 * <p>
 * <h2>Gameplay Utilities:</h2>
 * </p>
 * <table border="1">
 * <tr><td><b>Name</b></td><td><b>Description</b></td></tr>
 * <tr><td>{@linkplain Audio}</td><td>Provides a single point of access through 
 *         which sound effects and music can be played.</td></tr>
 * <tr><td>{@linkplain Camera}</td><td>Abstract class which can be used to 
 *         create specialized objects that will render the current perspective 
 *         of a viewport.</td></tr>
 * <tr><td>{@linkplain Command}</td><td>Allows an interactive component of an 
 *         input device to be coupled to some player action.</td></tr>
 * <tr><td>{@linkplain Entity}</td><td>Abstract class which can be used to 
 *         represent dynamic game objects in the game world.</td></tr>
 * <tr><td>{@linkplain Event}</td><td>Represents a game or engine event (such 
 *         as a pause, cutscene, or error) that temporarily disrupts the normal 
 *         flow of execution.</td></tr>
 * <tr><td>{@linkplain Game}</td><td>Provides utilities for managing high-level 
 *         game logic.</td></tr>
 * <tr><td>{@linkplain InputDevice}</td><td>Represents a peripheral device that 
 *         can capture input actions from a user.</td></tr>
 * <tr><td>{@linkplain Observable}</td><td>Used to relay information about 
 *         state changes occurring in the implementing object to one or more 
 *         observers located anywhere in the codebase.</td></tr>
 * <tr><td>{@linkplain Puppet}</td><td>Component object that enables 
 *         implementing objects to make use of input actions captured from an 
 *         input device.</td></tr>
 * <tr><td>{@linkplain Scene}</td><td>A 3D representation of the game world 
 *         that contains entities, light sources, and camera objects.</td></tr>
 * <tr><td>{@linkplain Skybox}</td><td>An object which can be used to create a 
 *         background to a scene.</td></tr>
 * <tr><td>{@linkplain Text}</td><td>Provides utilities for rendering text to 
 *         the screen.</td></tr>
 * <tr><td>{@linkplain Timer}</td><td>A simple monotonic timing 
 *         mechanism.</td></tr>
 * <tr><td>{@linkplain Widget}</td><td>An abstract class which can be used to 
 *         define subclasses that will comprise individual elements of a user 
 *         interface.</td></tr>
 * </td></tr>
 * </table>
 * <p>
 * <h2>Other Miscellaneous Features:</h2>
 * </p>
 * <table border="1">
 * <tr><td><b>Name</b></td><td><b>Description</b></td></tr>
 * <tr><td>{@linkplain ErrorUtils}</td><td>Provides convenience methods for 
 *         locating errors encountered by the engine at runtime.</td></tr>
 * <tr><td>{@linkplain Font}</td><td>Supplies the data parsed from a font file 
 *         as an immutable object which can be used to render text in some 
 *         desired font.</td></tr>
 * <tr><td>{@linkplain Song}</td><td>Represents a musical composition.</td></tr>
 * <tr><td>{@linkplain Sound}</td><td>Supplies the data parsed from an audio 
 *         file into a new sound object that can be used by the {@link Audio} 
 *         class</td></tr>
 * </table>
 * 
 * @see XJGE
 */
package dev.theskidster.xjge2.core;