import { AboutMe } from "./AboutMe";
import { Avatar } from "./Avatar";
import { Contact } from "./Contact";
import { Organizations } from "./Organizations";
import { MyProjects } from "./Projects";

export function Home() {
	return (
		<div className="w-full flex-1">
			<div className="flex flex-col items-center gap-2">
				<Avatar />
				<div className="font-semibold text-xl">Nguyen Phuc Vinh</div>
				<Contact />
			</div>

			<hr className="w-full my-4" />
			<div className="flex gap-4 flex-col sm:flex-row items-center sm:items-start sm justify-center sm:gap-6">
				<AboutMe />
				<MyProjects />
				<Organizations />
			</div>
		</div>
	);
}
