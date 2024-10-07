import { Avatar } from "./Avatar";
import { Contact } from "./Contact";

export function Home() {
	return (
		<div className="w-full flex flex-col items-center gap-4">
			<Avatar />
			<div className="font-semibold text-xl">Nguyen Phuc Vinh (Vincent)</div>
			<Contact />
		</div>
	);
}
