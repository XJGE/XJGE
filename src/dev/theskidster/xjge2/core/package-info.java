/**
 * Contains various classes, objects, and utilities many of which are essential to function of the engine. These are listed in greater 
 * detail below:
 * <br><br>
 * <h2>Core Classes:</h2>
 * <table border="1">
 * <tr><td><b>Name</b></td><td><b>Description</b></td></tr>
 * <tr><td>{@linkplain Hardware}</td><td>Manages the state of various peripheral devices including those used for audio output and 
 * visual display.</td></tr>
 * <tr><td>{@linkplain Input}</td><td>Provides a single point of access through which the state of peripheral input devices may be 
 * managed.</td></tr>
 * <tr><td>{@linkplain Monitor}</td><td>Represents a visual display device such as a computer monitor or television.</td></tr>
 * <tr><td>{@linkplain Viewport}</td><td>Represents a rectangular region of the applications window through which the perspective 
 * of a scene and its rendered objects may be viewed.</td></tr>
 * <tr><td>{@linkplain Window}</td><td>Provides a point of access which can be used to alter the properties of the applications window at runtime.</td></tr>
 * </table>
 * <br><br>
 * <h2>Gameplay Utilities:</h2>
 * <table border="1">
 * <tr><td><b>Name</b></td><td><b>Description</b></td></tr>
 * <tr><td>{@linkplain Audio}</td><td>asdf</td></tr>
 * </table>
 * <br><br>
 * <h2>Other Miscellaneous Features:</h2>
 * <table border="1">
 * <tr><td><b>Name</b></td><td><b>Description</b></td></tr>
 * <tr><td>{@linkplain ErrorUtils}</td><td>Provides convenience methods for locating errors encountered by the engine at runtime.</td></tr>
 * </table>
 */
package dev.theskidster.xjge2.core;


/**
 * Contains classes and objects essential to the function of the engine. These include:
 * <ul>
 * <li>{@link Hardware}</li>
 * <li>{@link Input}</li>
 * <li>{@link Monitor}</li>
 * <li>{@link Viewport}</li>
 * <li>{@link Window}</li>
 * </ul>
 * Additionally, this package contains various features useful for gameplay systems:
 * <table><td><tr>
 * <ul>
 * <li>{@link Audio}</li>
 * <li>{@link Camera}</li>
 * <li>{@link Command}</li>
 * <li>{@link Entity}</li>
 * <li>{@link Event}</li>
 * </ul>
 * </td><td>
 * <ul>
 * <li>{@link Game}</li>
 * <li>{@link InputDevice}</li>
 * <li>{@link Observable}</li>
 * <li>{@link Puppet}</li>
 * <li>{@link Scene}</li>
 * </ul>
 * </td><td>
 * <ul>
 * <li>{@link Skybox}</li>
 * <li>{@link Speaker}</li>
 * <li>{@link Text}</li>
 * <li>{@link Timer}</li>
 * <li>{@link Widget}</li>
 * </ul>
 * </td></tr></table>
 * Some other miscellaneous utilities are also worth mentioning here:
 * <ul>
 * <li>{@link ErrorUtils}</li>
 * <li>{@link Font}</li>
 * <li>{@link Skybox}</li>
 * <li>{@link Song}</li>
 * <li>{@link Sound}</li>
 * </ul>
 */