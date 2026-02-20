Event System
This event system is designed to serve the purpose of high flexibility and providing a clean and simple API.

Classes
🏤 EventMaster
The event master serves as a distributor of any kind of event, meaning any class, implementing the event interface.
By calling one single method (push), you can provide every port with the given event. Keep in mind, that this event does not get duplicated and you cannot provide any distribution order.

👂 Event Port
The event port is an abstract class, meant to be your direct connection to the event master.
After creating any kind of event port, you can register it to any amount of event masters, which then hold a weak reference to this port, providing it with incoming events.

The base abstract class does only provide basic event receival and an abstract method for you to overwrite, in order to implement own port logic.

Every event port also holds an event filter, the abstract method for you to overwrite will only ever receive events that have already passed this filter.
In the rare case of you also wanting to receive unfiltered events, the main receiving method, which is a protected method, is not final and can therefore also be overwritten by you.

The repository also ships with two event port definitions:

Active Event Port : Requires callbacks which get called for every valid event.
Passive Event Port : Acts like an event queue, storing events until you 'grab' or 'peek' them.
🔍 Event Filter
An event filter provides you of two layers, in order to filter out unwanted events.

The first layer is the layer of interests, being a list of subclasses of the event class, or rather implementations, which you want to receive.
Any event which is not of those interests, will be filtered out.
In case of an empty interests list, every event passes through this layer.

The second layer consists of callbacks, which take in an event and return a boolean.
This layer is meant for you to pass custom checks.
Every event needs to pass all of those methods, before passing this layer.
Keep in mind, that the methods get passed the original event, provided to the filter, so making changes to the event is not recommended.